// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.consts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-11
 */
public class DataIntegrityConsts {

  /* dataType = DECIMAL */

  public static final String DECIMAL_INT_DIGITS_KEY = "intDigits";
  public static final int DECIMAL_INT_DIGITS_VALUE = 14;

  public static final String DECIMAL_DEC_DIGITS_KEY = "decDigits";
  public static final int DECIMAL_DEC_DIGITS_VALUE_ONE = 1;
  public static final int DECIMAL_DEC_DIGITS_VALUE_TWO = 2;
  public static final int DECIMAL_DEC_DIGITS_VALUE_THREE = 3;
  public static final int DECIMAL_DEC_DIGITS_VALUE_FOUR = 4;
  public static final Set<Integer> DECIMAL_DEC_DIGITS_VALUESET =
      new HashSet<Integer>(Arrays.asList(DECIMAL_DEC_DIGITS_VALUE_ONE,
                                         DECIMAL_DEC_DIGITS_VALUE_TWO,
                                         DECIMAL_DEC_DIGITS_VALUE_THREE,
                                         DECIMAL_DEC_DIGITS_VALUE_FOUR));

  public static final Set<String> DECIMAL_KEYSET =
      new HashSet<String>(Arrays.asList(DECIMAL_INT_DIGITS_KEY,
                                        DECIMAL_DEC_DIGITS_KEY));

  /* dataType = DATETIME */

  public static final String DATETIME_DISPLAY_MODE_KEY = "displayMode";
  public static final int DATETIME_DISPLAY_MODE_VALUE_DATE = 0;
  public static final int DATETIME_DISPLAY_MODE_VALUE_DATETIME = 1;
  public static final Set<Integer> DATETIME_DISPLAY_MODE_VALUESET =
      new HashSet<Integer>(Arrays.asList(DATETIME_DISPLAY_MODE_VALUE_DATE,
                                         DATETIME_DISPLAY_MODE_VALUE_DATETIME));

  public static final Set<String> DATETIME_KEYSET =
      new HashSet<String>(Arrays.asList(DATETIME_DISPLAY_MODE_KEY));

  /* dataType = SINGLE_PICKLIST */

  public static final String SINGLE_PICKLIST_DEFAULT_VALUE_INDEX_KEY = "defaultValueIndex";

  public static final Set<String> SINGLE_PICKLIST_KEYSET =
      new HashSet<String>(Arrays.asList(SINGLE_PICKLIST_DEFAULT_VALUE_INDEX_KEY));

  /* dataType = CONTAINER */

  public static final String CONTAINER_DISPLAY_MODE_KEY = "displayMode";
  public static final int CONTAINER_DISPLAY_MODE_VALUE_COMPLETE = 1;
  public static final int CONTAINER_DISPLAY_MODE_VALUE_COMPACT = 2;
  public static final int CONTAINER_DISPLAY_MODE_VALUE_ADDRESS = 3;
  public static final Set<Integer> CONTAINER_DISPLAY_MODE_VALUESET =
      new HashSet<Integer>(Arrays.asList(CONTAINER_DISPLAY_MODE_VALUE_COMPLETE,
                                         CONTAINER_DISPLAY_MODE_VALUE_COMPACT,
                                         CONTAINER_DISPLAY_MODE_VALUE_ADDRESS));

  public static final Set<String> CONTAINER_KEYSET =
      new HashSet<String>(Arrays.asList(CONTAINER_DISPLAY_MODE_KEY));

}
