package hr.wozai.service.user.server.helper;

import java.math.BigDecimal;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/10/26
 */
public class CalcProgressHelper {
  public static String calcProgressByDifferentAmount(
          String startingAmountString, String goalAmountString, String currentAmountString) {
    BigDecimal startingAmount = new BigDecimal(startingAmountString);
    BigDecimal goalAmount = new BigDecimal(goalAmountString);
    BigDecimal currentAmount = new BigDecimal(currentAmountString);

    if (startingAmount.compareTo(goalAmount) < 0) {
      if (currentAmount.compareTo(goalAmount) >= 0) {
        return new BigDecimal(1).toString();
      } else if (currentAmount.compareTo(startingAmount) <= 0) {
        return new BigDecimal(0).toString();
      } else {
        return (currentAmount.subtract(startingAmount).
                divide(goalAmount.subtract(startingAmount), 2, BigDecimal.ROUND_HALF_UP)).toString();
      }
    } else if (startingAmount.compareTo(goalAmount) > 0) {
      if (currentAmount.compareTo(startingAmount) >= 0) {
        return new BigDecimal(0).toString();
      } else if (currentAmount.compareTo(goalAmount) <= 0) {
        return new BigDecimal(1).toString();
      } else {
        return (startingAmount.subtract(currentAmount).
                divide(startingAmount.subtract(goalAmount), 2, BigDecimal.ROUND_HALF_UP)).toString();
      }
    } else {
      if (currentAmount.compareTo(startingAmount) >= 0) {
        return new BigDecimal(1).toString();
      } else {
        return new BigDecimal(0).toString();
      }
    }
  }
}
