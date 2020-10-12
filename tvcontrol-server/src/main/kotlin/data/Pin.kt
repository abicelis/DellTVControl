package data


//BCM pins used by Action.kt for the gpio utility
//Check README.md or run 'gpio readall' on RPi for more info
enum class Pin(val BCMPin: Int) {
                                //Brown - GND
    POWER_SENSE(25),    //Red
    MACRO_4(24),        //Orange
    MACRO_2(23),        //Yellow
    MACRO_1(22),        //Green
    MACRO_3(27),        //Blue
    POWER(18)           //Gray
}