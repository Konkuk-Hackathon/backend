package backend.chat.service;

public enum Guest {

    YBJ("classpath:/prompts/ybj-template.st");

    private final String templateLocation;

    Guest(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }
}
