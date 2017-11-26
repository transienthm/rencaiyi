/**
 * TODO: 需要超级管理员权限, 方可调整 MySQL 时区, 以及设置 函数 调用, 故此次未能使用自定义函数 FUNCTION.
 */

/**
 * 创建指定字符集的数据库
 */
DROP PROCEDURE IF EXISTS CREATE_DATABASE;
DELIMITER $$
CREATE PROCEDURE CREATE_DATABASE (
	IN p_database VARCHAR (200),
	IN p_charset VARCHAR (200)
)
BEGIN
	SET @command = CONCAT (
		"CREATE DATABASE IF NOT EXISTS", " ",
		p_database, " ",
		"CHARACTER SET", " ",
		p_charset
	);
	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	DEALLOCATE PREPARE prepare_command;
END
$$
DELIMITER ;

/**
 * 创建指定数据表
 */
DROP PROCEDURE IF EXISTS CREATE_TABLE;
DELIMITER $$
CREATE PROCEDURE CREATE_TABLE (
	IN p_database VARCHAR (200),
	IN p_table VARCHAR (200)
)
BEGIN
	SET @command = CONCAT (
		"CREATE TABLE IF NOT EXISTS", " ",
		p_database, ".", p_table, " ",
		"(",
  			"`cloud_id` BIGINT(20) NOT NULL AUTO_INCREMENT,", " ",
  			"`org_id` BIGINT(20) NOT NULL,", " ",
  			"`survey_activity_id` BIGINT(20) NOT NULL,", " ",
  			"`survey_item_id` BIGINT(20) NOT NULL,", " ",
				"`cloud_version` BIGINT(20) NOT NULL DEFAULT 0,", " ",
  			"`label_cloud` JSON,", " ",
  			"`created_time` BIGINT(20) NOT NULL,", " ",
  			"`last_modified_time` BIGINT(20) NOT NULL,", " ",
  			"`is_deleted` TINYINT(1) NOT NULL DEFAULT 0,", " ",
  			"PRIMARY KEY (`cloud_id`)",
  		")",
		"ENGINE=INNODB", " ",
		"DEFAULT CHARSET=UTF8", " ",
		"ROW_FORMAT=COMPRESSED;"
	);

	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	DEALLOCATE PREPARE prepare_command;
END
$$
DELIMITER ;

/**
 * 初始化指定数据表
 */
DROP PROCEDURE IF EXISTS INIT_TABLE;
DELIMITER $$
CREATE PROCEDURE INIT_TABLE (
	IN p_database VARCHAR (200),
	IN p_table VARCHAR (200)
)
BEGIN
	SET @command = CONCAT (
			"INSERT INTO", " ",
			p_database, ".", p_table, " ",
			"(",
				"cloud_id,",
				"org_id,",
				"survey_activity_id,",
				"survey_item_id,",
				"cloud_version,",
				"label_cloud,",
				"created_time,",
				"last_modified_time,",
				"is_deleted",
			")", " ",
			"VALUES",
			"(",
				"1,",
				"0,",
				"0,",
				"0,",
				"0,",
				"NULL,",
				"ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000),",
				"ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000),",
				"0",
			");"
	);

	PREPARE prepare_command FROM @command;
	EXECUTE prepare_command;
	DEALLOCATE PREPARE prepare_command;
END
$$
DELIMITER ;

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

/**
 * 主处理部分
 */
DROP PROCEDURE IF EXISTS MAIN;
DELIMITER $$
CREATE PROCEDURE MAIN ()
BEGIN
	CALL CREATE_DATABASE ("user_org_db", "UTF8");
	CALL CREATE_TABLE ("user_org_db", "label_cloud");
		CALL ADD_UNIQUE_INDEX (
	  "user_org_db",
	  "label_cloud",
	  "unique_organization_activity_question_version_delete",
	  "org_id, survey_activity_id, survey_item_id, cloud_version, is_deleted"
	);
	CALL INIT_TABLE ("user_org_db", "label_cloud");
END
$$
DELIMITER ;

/**
 * SQL 脚本入口
 */
CALL MAIN ();