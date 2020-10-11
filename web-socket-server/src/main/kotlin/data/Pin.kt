package data


//BCM pins used by Action.kt for the gpio utility
//Check README.md or run 'gpio readall' on RPi for more info
enum class Pin(val BCMPin: Int) {
    POWER_SENSE(17),
    MACRO_4(18),
    MACRO_2(27),
    MACRO_1(22),
    MACRO_3(23),
    POWER(24)
}