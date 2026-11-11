package my.celium.org.darkmode.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import my.celium.org.config.ConfigValue;
import my.celium.org.darkmode.DarkModeMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** The settings. Texture changes apply when this closes, because they need a resource reload. */
public final class DarkModeScreen extends Screen {
    private static final int COLUMN = 150;
    private static final int GAP = 4;
    private static final int ROW = 24;
    private static final int MIN_BRIGHTNESS = 8;
    private static final int MAX_BRIGHTNESS = 60;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int HINT = 0xFFA0A0A0;

    private final Screen parent;
    private EditBox skipped;
    /** What happened to the last image dropped onto this screen, shown at the bottom. */
    private Component dropStatus;

    public DarkModeScreen(Screen parent) {
        super(Component.translatable("darkmode.settings.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = this.width / 2 - COLUMN - GAP / 2;
        int right = this.width / 2 + GAP / 2;
        int top = 30;

        toggle(left, top, "enabled", DarkModeMod.enabled);
        cycle(left, top + ROW, "theme", Theme.values(), DarkModeMod.theme(), DarkModeMod.theme);
        addRenderableWidget(new BrightnessSlider(left, top + ROW * 2));
        cycle(left, top + ROW * 3, "loading", LoadingColor.values(), DarkModeMod.loadingColor(),
                DarkModeMod.loadingScreen);

        toggle(right, top, "mods", DarkModeMod.modScreens);
        toggle(right, top + ROW, "hud", DarkModeMod.hud);
        toggle(right, top + ROW * 2, "backgrounds", DarkModeMod.drawnBackgrounds);
        toggle(right, top + ROW * 3, "text", DarkModeMod.readableText);

        Component skippedLabel = Component.translatable("darkmode.settings.skipped");
        skipped = new EditBox(this.font, left, top + ROW * 4 + 10, COLUMN * 2 + GAP, 20, skippedLabel);
        skipped.setMaxLength(512);
        skipped.setHint(skippedLabel);
        skipped.setValue(String.join(", ", DarkModeMod.skippedMods.get()));
        skipped.setTooltip(Tooltip.create(Component.translatable("darkmode.settings.skipped.tip")));
        addRenderableWidget(skipped);

        int imageRow = top + ROW * 5 + 14;
        addRenderableWidget(Button.builder(Component.translatable("darkmode.settings.image"), button -> openImageFolder())
                .bounds(left, imageRow, COLUMN * 2 + GAP, 20).tooltip(tip("image")).build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds(this.width / 2 - 100, this.height - 26, 200, 20).build());
    }

    private void toggle(int x, int y, String name, ConfigValue<Boolean> value) {
        Function<Boolean, Component> label = on -> Component.translatable("darkmode.settings." + name)
                .append(": ").append(on ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
        addRenderableWidget(Button.builder(label.apply(value.get()), button -> {
            value.set(!value.get());
            button.setMessage(label.apply(value.get()));
        }).bounds(x, y, COLUMN, 20).tooltip(tip(name)).build());
    }

    private <E extends Enum<E>> void cycle(int x, int y, String name, E[] choices, E current, ConfigValue<String> value) {
        Function<E, Component> label = choice -> Component.translatable("darkmode.settings." + name).append(": ")
                .append(Component.translatable("darkmode." + name + "." + choice.name().toLowerCase(Locale.ROOT)));
        addRenderableWidget(Button.builder(label.apply(current), button -> {
            int next = (Arrays.asList(choices).indexOf(selected(choices, value)) + 1) % choices.length;
            value.set(choices[next].name());
            button.setMessage(label.apply(choices[next]));
        }).bounds(x, y, COLUMN, 20).tooltip(tip(name)).build());
    }

    private static <E extends Enum<E>> E selected(E[] choices, ConfigValue<String> value) {
        return Arrays.stream(choices).filter(choice -> choice.name().equalsIgnoreCase(value.get().trim())).findFirst()
                .orElse(choices[0]);
    }

    private static Tooltip tip(String name) {
        return Tooltip.create(Component.translatable("darkmode.settings." + name + ".tip"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 12, TEXT);
        graphics.centeredText(this.font, Component.translatable("darkmode.settings.image.drop"), this.width / 2,
                30 + ROW * 5 + 14 + 26, HINT);
        graphics.centeredText(this.font, dropStatus != null ? dropStatus : Component.translatable("darkmode.settings.hint"),
                this.width / 2, this.height - 40, HINT);
    }

    /** An image dropped onto this screen becomes the loading screen. */
    @Override
    public void onFilesDrop(List<Path> files) {
        for (Path file : files) {
            if (CustomLoading.importImage(file)) {
                DarkModeMod.loadingScreen.set(LoadingColor.CUSTOM.name());
                dropStatus = Component.translatable("darkmode.settings.image.done");
                rebuildWidgets();
                return;
            }
        }
        dropStatus = Component.translatable("darkmode.settings.image.bad");
    }

    /** Opens config/darkmode, where loading.png goes. The call moved between Minecraft versions. */
    private static void openImageFolder() {
        Path folder = CustomLoading.file().getParent();
        try {
            Files.createDirectories(folder);
            try {
                Class.forName("com.mojang.blaze3d.Blaze3D").getMethod("openPath", Path.class).invoke(null, folder);
            } catch (NoSuchMethodException e) {
                Object os = Class.forName("net.minecraft.util.Util").getMethod("getPlatform").invoke(null);
                os.getClass().getMethod("openPath", Path.class).invoke(os, folder);
            }
        } catch (ReflectiveOperationException | java.io.IOException | RuntimeException e) {
            Failures.report("open " + folder, e);
        }
    }

    @Override
    public void onClose() {
        List<String> mods = Arrays.stream(skipped.getValue().split(",")).map(String::trim)
                .map(mod -> mod.toLowerCase(Locale.ROOT)).filter(mod -> !mod.isEmpty()).toList();
        if (!mods.equals(DarkModeMod.skippedMods.get())) {
            DarkModeMod.skippedMods.set(mods);
        }
        DarkModeMod.config.save();
        this.minecraft.setScreenAndShow(parent);
    }

    private static final class BrightnessSlider extends AbstractSliderButton {
        BrightnessSlider(int x, int y) {
            super(x, y, COLUMN, 20, Component.empty(), (double) (DarkModeMod.brightness.get() - MIN_BRIGHTNESS)
                    / (MAX_BRIGHTNESS - MIN_BRIGHTNESS));
            setTooltip(tip("brightness"));
            updateMessage();
        }

        private int percent() {
            return MIN_BRIGHTNESS + (int) Math.round(this.value * (MAX_BRIGHTNESS - MIN_BRIGHTNESS));
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("darkmode.settings.brightness").append(": " + percent() + "%"));
        }

        @Override
        protected void applyValue() {
            DarkModeMod.brightness.set(percent());
        }
    }
}
