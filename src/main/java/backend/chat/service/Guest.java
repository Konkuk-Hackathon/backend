package backend.chat.service;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Guest {

    YBJ("유병재","ybj","classpath:/prompts/ybj-template.st");

    private final String guestName;
    private final String guestCode;
    private final String templateLocation;

    Guest(String guestName, String guestCode, String templateLocation) {
        this.guestName = guestName;
        this.guestCode = guestCode;
        this.templateLocation = templateLocation;
    }

    public static Guest fromCode(String guestCode) {
        return Arrays.stream(Guest.values())
                .filter(guest -> guest.getGuestCode().equals(guestCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown guest code: " + guestCode));
    }
}