/**
 * Autogenerated by Thrift Compiler (0.9.3)
 * <p/>
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */
package tplatform;

public enum ELoginError implements org.apache.thrift.TEnum {

    success(0), userNotExist(1), passwordError(2), unkown(3);

    private final int value;

    private ELoginError(int value) {
        this.value = value;
    }

    /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
    public static ELoginError findByValue(int value) {
        switch(value) {
            case 0:
                return success;
            case 1:
                return userNotExist;
            case 2:
                return passwordError;
            case 3:
                return unkown;
            default:
                return null;
        }
    }

    /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
    public int getValue() {
        return value;
    }
}
