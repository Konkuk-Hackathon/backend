package backend.chat.controller.api;

import java.util.List;

public record GuestCodeResponse(List<GuestCode> guestCodes) {

    public static record GuestCode(String guestName, String guestCode) {}
}
