package hr.wozai.service.feed.client.enums;

/**
 * Created by wangbin on 2016/11/20.
 */
public enum RewardMedalTemplate {

    REWARD_MEDAL_THINKER(1, 0, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/4.png", "思想者", "思考有深度，解决难点问题"),
    REWARD_MEDAL_PROFESSIONAL(2, 0, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/10.png", "最专业", "专业度满级，难点问题手到擒来"),
    REWARD_MEDAL_CREATIVE(3, 0, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/6.png", "有创意", "突破定势，创新带来价值"),
    REWARD_MEDAL_GENEROUS(4, 0, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/9.png", "最慷慨", "慷慨帮助，助力他人的成就与成长"),
    REWARD_MEDAL_EXECUTIVE(5, 0, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/7.png", "执行力", "神速搞定，执行力一骑绝尘"),
    REWARD_MEDAL_WINNER(6, 0, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/2.png", "夺标奖", "勇夺大单，获得傲人业绩"),

    REWARD_MEDAL_WARRIORS(7, 1, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/5.png", "先锋勋章", "冲锋在前，取得卓越成就"),
    REWARD_MEDAL_PATRON(8, 1, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/3.png", "靠山勋章", "为其它团队提供强有力的支持"),
    REWARD_MEDAL_CREATIVITY(9, 1, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/1.png", "创新勋章", "最具创造力的高智组合"),
    REWARD_MEDAL_EXECUTION(10, 1, "http://front-emoji.oss-cn-beijing.aliyuncs.com/reward-medal/40*40/8.png", "执行勋章", "团队执行力优异夺目"),
    ;

    private String medalIcon;
    private String medalName;
    private String medalDescription;
    private int medalType;
    private int code;

    RewardMedalTemplate(int code, int medalType, String medalIcon, String medalName, String medalDescription) {
        this.code = code;
        this.medalType = medalType;
        this.medalIcon = medalIcon;
        this.medalName = medalName;
        this.medalDescription = medalDescription;
    }

    public static RewardMedalTemplate getRewardMedalTemplateByCode(int code) {
        for (RewardMedalTemplate rewardMedalTemplate : RewardMedalTemplate.values()) {
            if (rewardMedalTemplate.code == code) {
                return rewardMedalTemplate;
            }
        }
        return null;
    }

    public String getMedalIcon() {
        return medalIcon;
    }

    public String getMedalName() {
        return medalName;
    }

    public String getMedalDescription() {
        return medalDescription;
    }

    public int getMedalType() {
        return medalType;
    }

    public int getCode() {
        return code;
    }
}
