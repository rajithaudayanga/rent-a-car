package com.example.rentacar.Controllers;

public class DatabaseFields {
    public static class constants {
        public static final String USER_TYPE_CUSTOMER = "customer";
        public static final String USER_TYPE_SERVICE = "rent_service";
        public static final String PROCESS_TYPE_REGISTER = "process_type_register";
        public static final String PROCESS_TYPE_EDIT = "process_type_edit";
        public static final String VEHICLE_AVAILABILITY_AVAILABLE = "available";
        public static final String VEHICLE_AVAILABILITY_NOT_AVAILABLE = "not_available";
    }

    public static class userFields {
        public static final String PROFILE_LEVEL = "profile_level";
        public static final String USER_TYPE = "user_type";
        public static final String PROFILE_PHOTO = "profile_photo";
        public static final String MOBILE = "mobile";
    }

    public static class customerFields{
        public static final String NAME = "name";
    }

    public static class serviceFields {
        public static final String SERVICE_NAME = "name";
        public static final String TELEPHONE = "telephone";
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String TOWN = "town";
        public static final String LOCATION = "location";
    }

    public static class sharedPrefferenceData {
        public static final String FILE_NAME = "user_data";
    }

    public static class vehicleFields {
        public static final String IMAGE_1_URL = "image_1_url";
        public static final String IMAGE_2_URL = "image_2_url";
        public static final String IMAGE_3_URL = "image_3_url";
        public static final String IMAGE_4_URL = "image_4_url";
        public static final String AVAILABILITY = "availability";
    }

    public static class notification {
        public static final String NOTIFICATION_TYPE_CHECK_VEHICLE = "check_vehicle";
        public static final String NOTIFICATION_TYPE_RESPONSE_AVAILABLE_VEHICLE = "response_available_vehicle";
        public static final String NOTIFICATION_TYPE_RESPONSE_NOT_AVAILABLE_VEHICLE = "response_not_available_vehicle";
        public static final String NOTIFICATION_TYPE_VEHICLE_BOOKING = "vehicle_booking";
        public static final String NOTIFICATION_TYPE_VEHICLE_BOOKING_APPROVED = "vehicle_booking approved";
        public static final String NOTIFICATION_TYPE_VEHICLE_BOOKING_CANCEL = "vehicle_booking_cancel";
        public static final String NOTIFICATION_STATUS_UNREAD = "unread";
        public static final String NOTIFICATION_STATUS_READ = "read";
        public static final String NOTIFICATION_NEW_MESSAGE = "new message";
    }

    public static class booking {
        public static final String PENDING_APPROVEL= "Pending for Approval Booking";
        public static final String APPROVED= "Approved Booking";
    }

    public static class rentals {
        public static final String ON_GONING= "On Going";
        public static final String COMPLETED= "Completed";
    }

    public static class messages {
        public static final String UNREAD= "unread";
        public static final String READ= "read";
    }

    public static class connection {
        public static final String SUCCESS= "Connection is available";
        public static final String FAIL= "There is no active internet connection";
        public static final String NO_CONNECTION= "Device not connected to internet";
    }
}
