package backend.chat.controller.api;

import java.util.List;

public record ThreadsOfMember(List<ChatsOfThread> threadsOfMember) {}
