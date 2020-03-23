package com.grayhatdevelopers.kontrol.utils

object AppConstants {

    /**
     * App Constants
     */
    const val STARTING_DATE = "01/Jan/70 00:00:00"
    const val TOKEN = "Token"
    const val COMPANY_NAME = "SALOON DEZINERS"

    /**
     * REGEX
     */
    const val REGEX_PHONE_NUMBER =
        "((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}\$|^\\d{11}\$|^\\d{4}-\\d{7}\$"
    const val REGEX_EMAIL_ADDRESS = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    const val REGEX_CONTAIN_NUMBERS = ".*\\d.*"


    /**
     * Utils
     */
    const val DEFAULT_DATE_FORMAT = "dd/MM/YY"
    const val DEFAULT_TIME_FORMAT = "hh:mm:ss"
    const val DEFAULT_DATE_TIME_FORMAT = "$DEFAULT_DATE_FORMAT $DEFAULT_TIME_FORMAT"

    /**
     * Shared Preferences Keys
     */

    const val IS_RIDER_LOGGED_IN = "isRiderLoggedIn"
    const val SAVED_RIDER = "savedRider"
    const val SAVED_RIDER_TOKEN = "savedRiderToken"
    const val LAST_FETCH_TIME = "lastFetchTime"


    /**
     * MAPBOX token
     */
    const val MAP_BOX_TOKEN =
        "pk.eyJ1IjoiZ3JheWhhdGRldmVsb3BlcnMiLCJhIjoiY2s0YWNqNjVyMDJrMTNycDh2OTd5MTBpdiJ9.Lpq70iRl9mTIJA1q-cz7UQ"

    /**
     * Routes
     */
    private const val AWS_SERVER_PORT = "8081"
    const val AWS_SERVER_BASE_URL =
        "http://ec2-3-16-157-5.us-east-2.compute.amazonaws.com:$AWS_SERVER_PORT"
    const val RIDER_LOGIN_ROUTE = "/rider_login"
    const val GET_RIDER_TASKS = "/tasks"
    const val ADD_PAYMENT_TO_TASK_ROUTE = "/add_payment_to_task"


    /**
     * Labels
     */

    const val CREATE_NEW_TASKS = "     Create Payment"
    const val ACTIVE_TASKS = "     Active Tasks"
    const val TASK_HISTORY = "     My Tasks History"
    const val CHAT_MODE = "     Switch To Chat Mode"
    const val SIGN_OUT = "     Sign Out"
    const val HIDE_LABEL = " "

    /**
     * Http Status Codes
     */

    const val OK = 200
    const val ACCEPTED = 202
    const val BAD_REQUEST = 400
    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val NOT_ACCEPTABLE = 406

    /**
     * arrays
     */

    val TASK_TYPES = arrayOf("Regular", "Emergency", "Off Schedule")
    val TASK_MODELS = arrayOf("Invoice", "Payment", "Return")

}