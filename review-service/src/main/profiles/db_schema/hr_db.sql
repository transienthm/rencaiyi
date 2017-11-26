/**
 * TODO: 需要超级管理员权限, 方可调整 MySQL 时区, 以及设置 函数 调用, 故此次未能使用自定义函数 FUNCTION.
 */

/**
 * 创建指定字符集的数据库
 */
DROP PROCEDURE IF EXISTS CREATE_DATABASE;
DELIMITER $$
CREATE PROCEDURE CREATE_DATABASE (
	IN p_db_name VARCHAR (200),
	IN p_character_set VARCHAR (200)
)
BEGIN
	SET @command = CONCAT (
		"CREATE DATABASE IF NOT EXISTS", " ",
		p_db_name, " ",
		"CHARACTER SET", " ",
		p_character_set
	);
	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	DEALLOCATE PREPARE prepare_command;
END
$$
DELIMITER ;

/*CALL CREATE_DATABASE("hr_db_backup", "UTF8");*/

/**
 * 为指定数据库创建指定数据表，表结构拷贝自其他指定数据表
 */
DROP PROCEDURE IF EXISTS CREATE_TABLE;
DELIMITER $$
CREATE PROCEDURE CREATE_TABLE (
	IN p_target_db VARCHAR (200),
	IN p_target_table VARCHAR (200),
	IN p_source_db VARCHAR (200),
	IN p_source_table VARCHAR (200)
)
BEGIN
	SET @command = CONCAT (
		"DROP TABLE IF EXISTS", " ",
		p_target_db, ".", p_target_table
	);
	
	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	
	SET @command = CONCAT (
		"CREATE TABLE", " ",
		p_target_db, ".", p_target_table, " ", 
		"LIKE", " ",
		p_source_db, ".", p_source_table
	);
	
	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	
	DEALLOCATE PREPARE prepare_command;
END
$$
DELIMITER ;

/*CALL CREATE_TABLE ("hr_db_backup", "test_zich_temp", "hr_db", "test_zich");*/

/**
 * 删除指定数据库指定数据表中的指定索引
 */
DROP PROCEDURE IF EXISTS DROP_INDEX;
DELIMITER $$
CREATE PROCEDURE DROP_INDEX (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200),
	IN p_index_name VARCHAR (200)
)
BEGIN
	SET @command = CONCAT (
		"ALTER TABLE", " ",
		p_db_name, ".", p_table_name, " ",
		"DROP INDEX", " ",
		p_index_name
	);
	
	SELECT
		COUNT(*) INTO @count
	FROM (
			SELECT
				*
			FROM
				INFORMATION_SCHEMA.STATISTICS
			WHERE
				TABLE_SCHEMA = p_db_name
				AND TABLE_NAME = p_table_name
				AND INDEX_NAME = p_index_name
				AND INDEX_NAME <> "PRIMARY"
			GROUP BY
				INDEX_NAME
		) grouped_table;

	IF @count > 0 THEN
	    PREPARE prepare_command FROM @command;
	    EXECUTE prepare_command;
	    DEALLOCATE PREPARE prepare_command;
	END IF;
END
$$
DELIMITER ;

/*CALL DROP_INDEX ("hr_db", "test_zich_temp", "org_reviewee_reviewer");*/

/**
 * 删除指定数据库指定数据表中的所有索引
 */
DROP PROCEDURE IF EXISTS DROP_ALL_INDEXES;
DELIMITER $$
CREATE PROCEDURE DROP_ALL_INDEXES (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	DECLARE current_index CHAR(100);
	DECLARE cursor_done INT DEFAULT FALSE;

	DECLARE index_cursor CURSOR FOR
		SELECT
			INDEX_NAME
		FROM
			INFORMATION_SCHEMA.STATISTICS
		WHERE 
			TABLE_SCHEMA = p_db_name
			AND TABLE_NAME = p_table_name 
			AND INDEX_NAME <> "PRIMARY"
		GROUP BY INDEX_NAME;

	DECLARE CONTINUE HANDLER FOR NOT FOUND SET cursor_done = TRUE;
	OPEN index_cursor;
	
	current_loop: LOOP
		FETCH index_cursor INTO current_index;
		
		IF cursor_done THEN
			LEAVE current_loop;
		END IF;
		
		SET @command = CONCAT (
			"ALTER TABLE", " ",
			p_db_name, ".", p_table_name, " ",
			"DROP INDEX", " ",
			current_index
		);
		
		PREPARE prepare_command FROM @command;
	    EXECUTE prepare_command;
	    DEALLOCATE PREPARE prepare_command;
	END LOOP;
		
	CLOSE index_cursor;
END
$$
DELIMITER ;

/*CALL DROP_ALL_INDEXES ("hr_db", "test_zich_temp");*/

/**
 * 为指定数据库指定表创建指定唯一性索引
 */
DROP PROCEDURE IF EXISTS ADD_UNIQUE_INDEX;
DELIMITER $$
CREATE PROCEDURE ADD_UNIQUE_INDEX (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200),
	IN p_index_name VARCHAR (200),
	IN p_index_items VARCHAR (200)
)
BEGIN
	CALL DROP_INDEX (
		p_db_name,
		p_table_name,
		p_index_name
	);
	
	SET @command = CONCAT (
		"ALTER TABLE", " ",
		p_db_name, ".", p_table_name, " ",
		"ADD UNIQUE INDEX", " ",
		"`", p_index_name, "`", " ",
		"(", p_index_items, ")"
	);
	
	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	DEALLOCATE PREPARE prepare_command;
END
$$
DELIMITER ;

/*CALL ADD_UNIQUE_INDEX ("hr_db", "test_zich_temp", "test_unique_index_name", "org_id, template_id, reviewee_id");*/

/**
 * 为指定数据库指定表创建指定非唯一性索引
 */
DROP PROCEDURE IF EXISTS ADD_INDEX;
DELIMITER $$
CREATE PROCEDURE ADD_INDEX (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200),
	IN p_index_name VARCHAR (200),
	IN p_index_items VARCHAR (200)
)
BEGIN
	CALL DROP_INDEX (
		p_db_name,
		p_table_name,
		p_index_name
	);
	
	SET @command = CONCAT (
		"ALTER TABLE", " ",
		p_db_name, ".", p_table_name, " ",
		"ADD INDEX", " ",
		"`", p_index_name, "`", " ",
		"(", p_index_items, ")"
	);
	
	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	DEALLOCATE PREPARE prepare_command;
END
$$
DELIMITER ;

/*CALL ADD_INDEX ("hr_db", "test_zich_temp", "test_index_name", "org_id, template_id, reviewee_id");*/

/**
 * 向指定数据库的指定数据表中填充来自其它具有相同表结构的指定数据表中的数据
 */
DROP PROCEDURE IF EXISTS FILL_TABLE;
DELIMITER $$
CREATE PROCEDURE FILL_TABLE (
	IN p_target_db VARCHAR (200),
	IN p_target_table VARCHAR (200),
	IN p_source_db VARCHAR (200),
	IN p_source_table VARCHAR (200),
	IN p_index_items VARCHAR (200)
)
BEGIN
	SET @command = CONCAT (
		"INSERT INTO", " ",
		p_target_db, ".", p_target_table, " ",
		"SELECT * FROM", " ",
		p_source_db, ".", p_source_table, " ",
		"GROUP BY", " ",
		p_index_items
	);
	
	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	DEALLOCATE PREPARE prepare_command;
END
$$
DELIMITER ;

/*CALL FILL_TABLE ("hr_db_backup", "test_zich_temp", "hr_db", "test_zich", "org_id, template_id, reviewee_id");*/

/**
 * 将指定数据库中的指定表重命名为其它表名
 */
DROP PROCEDURE IF EXISTS RENAME_TABLE;
DELIMITER $$
CREATE PROCEDURE RENAME_TABLE (
	IN p_origin_db VARCHAR (200),
	IN p_origin_table VARCHAR (200),
	IN p_target_db VARCHAR (200),
	IN p_target_table VARCHAR (200)
)
BEGIN
	SELECT
		COUNT(*) INTO @count
	FROM
		INFORMATION_SCHEMA.TABLES
	WHERE
		TABLE_SCHEMA = p_target_db
		AND TABLE_NAME = p_target_table;
	
	IF @count <= 0 THEN
		SET @command = CONCAT (
			"ALTER TABLE", " ",
			p_origin_db, ".", p_origin_table, " ",
			"RENAME TO", " ",
			p_target_db, ".", p_target_table
		);
		
		PREPARE prepare_command FROM @command;
		EXECUTE prepare_command;
		DEALLOCATE PREPARE prepare_command;
	END IF;
END
$$
DELIMITER ;

/*CALL RENAME_TABLE ("hr_db", "test_zich_temp", "hr_db_backup", "test_zich_temp_01"); CALL RENAME_TABLE("hr_db_backup", "test_zich_temp_01", "hr_db", "test_zich_temp");*/

/**
 * 将指定数据库中的三个表名替换
 */
DROP PROCEDURE IF EXISTS RENAME_TABLES;
DELIMITER $$
CREATE PROCEDURE RENAME_TABLES (
	IN p_temp_db VARCHAR (200),
	IN p_temp_table VARCHAR (200),
	IN p_source_db VARCHAR (200),
	IN p_source_table VARCHAR (200),
	IN p_backup_db VARCHAR (200),
	IN p_backup_table VARCHAR (200)
)
BEGIN
	CALL RENAME_TABLE (p_source_db, p_source_table, p_backup_db, p_backup_table);
	CALL RENAME_TABLE (p_temp_db, p_temp_table, p_source_db, p_source_table);
END
$$
DELIMITER ;

/*CALL RENAME_TABLES ("hr_db", "test_zich_temp", "hr_db", "test_zich", "hr_db", "test_zich_backup");*/

/**
 * 自动创建指定字符集的数据库
 */
DROP PROCEDURE IF EXISTS AUTO_CREATE_DATABASE;
DELIMITER $$
CREATE PROCEDURE AUTO_CREATE_DATABASE (
	IN p_db_name VARCHAR (200)
)
BEGIN
	SET @backup_database = CONCAT (p_db_name, "_backup");
	CALL CREATE_DATABASE(@backup_database, "UTF8");
END
$$
DELIMITER ;

/*CALL AUTO_CREATE_DATABASE("hr_db");*/

/**
 * 自动创建数据表
 */
DROP PROCEDURE IF EXISTS AUTO_CREATE_TABLE;
DELIMITER $$
CREATE PROCEDURE AUTO_CREATE_TABLE (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	SET @temp_table = CONCAT (p_table_name, "_temp");
	CALL CREATE_TABLE (p_db_name, @temp_table, p_db_name, p_table_name);
END
$$
DELIMITER ;

/*CALL AUTO_CREATE_TABLE ("hr_db", "test_zich");*/

/**
 * 自动删除表中全部索引
 */
DROP PROCEDURE IF EXISTS AUTO_DROP_ALL_INDEXES;
DELIMITER $$
CREATE PROCEDURE AUTO_DROP_ALL_INDEXES (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	SET @temp_table = CONCAT (p_table_name, "_temp");
	CALL DROP_ALL_INDEXES (p_db_name, @temp_table);
END
$$
DELIMITER ;

/*CALL AUTO_DROP_ALL_INDEXES ("hr_db", "test_zich");*/

/**
 * 自动添加唯一性索引
 */
DROP PROCEDURE IF EXISTS AUTO_ADD_UNIQUE_INDEX;
DELIMITER $$
CREATE PROCEDURE AUTO_ADD_UNIQUE_INDEX (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200),
	IN p_index_name VARCHAR (200),
	IN p_index_items VARCHAR (200)
)
BEGIN
	SET @temp_table = CONCAT (p_table_name, "_temp");
	CALL ADD_UNIQUE_INDEX (p_db_name, @temp_table, p_index_name, p_index_items);
END
$$
DELIMITER ;

/*CALL AUTO_ADD_UNIQUE_INDEX ("hr_db", "test_zich", "test_zich_unique_index", "org_id, template_id, reviewee_id");*/

/**
 * 自动添加非唯一性索引
 */
DROP PROCEDURE IF EXISTS AUTO_ADD_INDEX;
DELIMITER $$
CREATE PROCEDURE AUTO_ADD_INDEX (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200),
	IN p_index_name VARCHAR (200),
	IN p_index_items VARCHAR (200)
)
BEGIN
	SET @temp_table = CONCAT (p_table_name, "_temp");
	CALL ADD_INDEX (p_db_name, @temp_table, p_index_name, p_index_items);
END
$$
DELIMITER ;

/*CALL AUTO_ADD_INDEX ("hr_db", "test_zich", "test_zich_index", "org_id, template_id");*/

/**
 * 自动填充表数据
 */
DROP PROCEDURE IF EXISTS AUTO_FILL_TABLE;
DELIMITER $$
CREATE PROCEDURE AUTO_FILL_TABLE (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200),
	IN p_index_items VARCHAR (200)
)
BEGIN
	SET @temp_table = CONCAT (p_table_name, "_temp");
	CALL FILL_TABLE (p_db_name, @temp_table, p_db_name, p_table_name, p_index_items);
END
$$
DELIMITER ;

/*CALL AUTO_FILL_TABLE ("hr_db", "test_zich", "org_id, template_id, reviewee_id");*/

/**
 * 自动重命名表名
 */
DROP PROCEDURE IF EXISTS AUTO_RENAME_TABLES;
DELIMITER $$
CREATE PROCEDURE AUTO_RENAME_TABLES (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	SET @temp_table = CONCAT (p_table_name, "_temp");
	SELECT DATE_FORMAT (NOW(), "%Y%m%d%H%I%S") INTO @backup_suffix;
	SET @backup_table = CONCAT (p_table_name, "_", @backup_suffix);
	SET @backup_database = CONCAT (p_db_name, "_backup");
	CALL RENAME_TABLES (p_db_name, @temp_table, p_db_name, p_table_name, @backup_database, @backup_table);
END
$$
DELIMITER ;

/*CALL AUTO_RENAME_TABLES ("hr_db", "test_zich");*/

/**
 * REVIEW_ACTIVITY 处理部分
 */
DROP PROCEDURE IF EXISTS PROCESS_REVIEW_ACTIVITY;
DELIMITER $$
CREATE PROCEDURE PROCESS_REVIEW_ACTIVITY (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	CALL AUTO_CREATE_TABLE (
		p_db_name,
		p_table_name
	);
	CALL AUTO_DROP_ALL_INDEXES (
		p_db_name,
		p_table_name
	);
	CALL AUTO_ADD_INDEX (
		p_db_name,
		p_table_name,
		"org_reviewee",
		"org_id, reviewee_id"
	);
	CALL AUTO_ADD_UNIQUE_INDEX (
		p_db_name,
		p_table_name,
		"org_template_reviewee_delete",
		"org_id, template_id, reviewee_id, is_deleted"
	);
	CALL AUTO_FILL_TABLE (
		p_db_name,
		p_table_name,
		"org_id, template_id, reviewee_id, is_deleted"
	);
	CALL AUTO_RENAME_TABLES (
		p_db_name,
		p_table_name
	);
END
$$
DELIMITER ;

/**
 * REVIEW_COMMENT 处理部分
 */
DROP PROCEDURE IF EXISTS PROCESS_REVIEW_COMMENT;
DELIMITER $$
CREATE PROCEDURE PROCESS_REVIEW_COMMENT (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	CALL AUTO_CREATE_TABLE (
		p_db_name,
		p_table_name
	);
	CALL AUTO_DROP_ALL_INDEXES (
		p_db_name,
		p_table_name
	);
	CALL AUTO_ADD_INDEX (
		p_db_name,
		p_table_name,
		"org_template_reviewee_reviewer",
		"org_id, template_id, reviewee_id, reviewer_id"
	);
	CALL AUTO_FILL_TABLE (
		p_db_name,
		p_table_name,
		"comment_id"
	);
	CALL AUTO_RENAME_TABLES (
		p_db_name,
		p_table_name
	);
END
$$
DELIMITER ;

/**
 * REVIEW_INVITATION 处理部分
 */
DROP PROCEDURE IF EXISTS PROCESS_REVIEW_INVITATION;
DELIMITER $$
CREATE PROCEDURE PROCESS_REVIEW_INVITATION (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	CALL AUTO_CREATE_TABLE (
		p_db_name,
		p_table_name
	);
	CALL AUTO_DROP_ALL_INDEXES (
		p_db_name,
		p_table_name
	);
	
	CALL AUTO_ADD_INDEX (
		p_db_name,
		p_table_name,
		"org_template_reviewer",
		"org_id, template_id, reviewer_id"
	);
	CALL AUTO_ADD_INDEX (
		p_db_name,
		p_table_name,
		"org_reviewee_reviewer",
		"org_id, reviewee_id, reviewer_id"
	);
	CALL AUTO_ADD_INDEX (
		p_db_name,
		p_table_name,
		"org_reviewer",
		"org_id, reviewer_id"
	);
	
	CALL AUTO_ADD_UNIQUE_INDEX (
		p_db_name,
		p_table_name,
		"org_template_reviewee_reviewer_delete",
		"org_id, template_id, reviewee_id, reviewer_id, is_deleted"
	);
	CALL AUTO_FILL_TABLE (
		p_db_name,
		p_table_name,
		"org_id, template_id, reviewee_id, reviewer_id, is_deleted"
	);
	
	CALL AUTO_RENAME_TABLES (
		p_db_name,
		p_table_name
	);
END
$$
DELIMITER ;

/**
 * REVIEW_INVITED_TEAM 处理部分
 */
DROP PROCEDURE IF EXISTS PROCESS_REVIEW_INVITED_TEAM;
DELIMITER $$
CREATE PROCEDURE PROCESS_REVIEW_INVITED_TEAM (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	CALL AUTO_CREATE_TABLE (
		p_db_name,
		p_table_name
	);
	CALL AUTO_DROP_ALL_INDEXES (
		p_db_name,
		p_table_name
	);
	CALL AUTO_ADD_UNIQUE_INDEX (
		p_db_name,
		p_table_name,
		"org_template_team_delete",
		"org_id, review_template_id, team_id, is_deleted"
	);
	CALL AUTO_FILL_TABLE (
		p_db_name,
		p_table_name,
		"org_id, review_template_id, team_id, is_deleted"
	);
	CALL AUTO_RENAME_TABLES (
		p_db_name,
		p_table_name
	);
END
$$
DELIMITER ;

/**
 * REVIEW_PROJECT 处理部分
 */
DROP PROCEDURE IF EXISTS PROCESS_REVIEW_PROJECT;
DELIMITER $$
CREATE PROCEDURE PROCESS_REVIEW_PROJECT (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	CALL AUTO_CREATE_TABLE (
		p_db_name,
		p_table_name
	);
	CALL AUTO_DROP_ALL_INDEXES (
		p_db_name,
		p_table_name
	);
	CALL AUTO_ADD_INDEX (
		p_db_name,
		p_table_name,
		"org_template_reviewee",
		"org_id, template_id, reviewee_id"
	);
	CALL AUTO_FILL_TABLE (
		p_db_name,
		p_table_name,
		"project_id"
	);
	CALL AUTO_RENAME_TABLES (
		p_db_name,
		p_table_name
	);
END
$$
DELIMITER ;

/**
 * REVIEW_QUESTION 处理部分
 */
DROP PROCEDURE IF EXISTS PROCESS_REVIEW_QUESTION;
DELIMITER $$
CREATE PROCEDURE PROCESS_REVIEW_QUESTION (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
	CALL AUTO_CREATE_TABLE (
		p_db_name,
		p_table_name
	);
	CALL AUTO_DROP_ALL_INDEXES (
		p_db_name,
		p_table_name
	);
	CALL AUTO_ADD_INDEX (
		p_db_name,
		p_table_name,
		"org_template",
		"org_id, template_id"
	);
	CALL AUTO_FILL_TABLE (
		p_db_name,
		p_table_name,
		"question_id"
	);
	CALL AUTO_RENAME_TABLES (
		p_db_name,
		p_table_name
	);
END
$$
DELIMITER ;

/**
 * REVIEW_TEMPLATE 处理部分
 */
DROP PROCEDURE IF EXISTS PROCESS_REVIEW_TEMPLATE;
DELIMITER $$
CREATE PROCEDURE PROCESS_REVIEW_TEMPLATE (
	IN p_db_name VARCHAR (200),
	IN p_table_name VARCHAR (200)
)
BEGIN
END
$$
DELIMITER ;

/**
 * TEST 部分
 */
DROP PROCEDURE IF EXISTS TEST;
DELIMITER $$
CREATE PROCEDURE TEST ()
BEGIN
  /* 创建测试用的临时数据表 */
  CALL CREATE_TABLE (
    "hr_db",
    "test_zich",
    "hr_db",
    "review_activity"
  );
  CALL FILL_TABLE (
    "hr_db",
    "test_zich",
    "hr_db",
    "review_activity",
    "activity_id"
  );

  /* 创建测试用的临时备份数据库 */
  CALL AUTO_CREATE_DATABASE("test_zich_db");

  /* 根据创建的测试用临时数据表进行 TEST 模拟操作 */
	CALL AUTO_CREATE_TABLE (
		"hr_db",
		"test_zich"
	);
	CALL AUTO_DROP_ALL_INDEXES (
		"hr_db",
		"test_zich"
	);
	CALL AUTO_ADD_INDEX (
		"hr_db",
		"test_zich",
		"test_zich_index",
		"org_id, reviewee_id"
	);
	CALL AUTO_ADD_UNIQUE_INDEX (
		"hr_db",
		"test_zich",
		"test_zich_unique_index",
		"org_id, template_id, reviewee_id, is_deleted"
	);
	CALL AUTO_FILL_TABLE (
		"hr_db",
		"test_zich",
		"org_id, template_id, reviewee_id, is_deleted"
	);
	CALL RENAME_TABLES(
	  "hr_db",
	  "test_zich_temp",
	  "hr_db",
	  "test_zich",
	  "test_zich_db_backup",
	  "test_zich_backup"
	);

	/* 测试完成后, 删除建立的测试用的临时数据表, 以及在 backup 库中创建的对应的备份表 */
	DROP TABLE IF EXISTS hr_db.test_zich;
	DROP TABLE IF EXISTS hr_db.test_zich_temp;
	DROP DATABASE IF EXISTS test_zich_db_backup;
END
$$
DELIMITER ;

/*CALL TEST ();*/

/**
 * 主处理部分
 */
DROP PROCEDURE IF EXISTS MAIN;
DELIMITER $$
CREATE PROCEDURE MAIN ()
BEGIN
	CALL AUTO_CREATE_DATABASE("hr_db");
	CALL PROCESS_REVIEW_ACTIVITY ("hr_db", "review_activity");
	CALL PROCESS_REVIEW_COMMENT ("hr_db", "review_comment");
	CALL PROCESS_REVIEW_INVITATION ("hr_db", "review_invitation");
	CALL PROCESS_REVIEW_INVITED_TEAM ("hr_db", "review_invited_team");
	CALL PROCESS_REVIEW_PROJECT ("hr_db", "review_project");
	CALL PROCESS_REVIEW_QUESTION ("hr_db", "review_question");
	CALL PROCESS_REVIEW_TEMPLATE ("hr_db", "review_template");
END
$$
DELIMITER ;

CALL MAIN ();