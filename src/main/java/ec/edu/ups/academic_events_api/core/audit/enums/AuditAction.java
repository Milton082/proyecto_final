package ec.edu.ups.academic_events_api.core.audit.enums;

public enum AuditAction {

    ACCOUNT_REGISTERED,

    LOGIN_SUCCESS,
    LOGIN_FAILED,

    REFRESH_TOKEN_SUCCESS,
    REFRESH_TOKEN_FAILED,

    LOGOUT_SUCCESS,
    LOGOUT_FAILED,

    USER_STATUS_UPDATED,
    USER_ROLES_UPDATED
}