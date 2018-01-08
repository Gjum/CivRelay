package gjum.minecraft.forge.civrelay.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiBase extends GuiScreen {
    public final GuiScreen parentScreen;
    public final List<ElementBase> elements = new ArrayList<>();
    public final List<GuiTextField> textFieldList = new ArrayList<>();
    public LayoutBoundingBox layoutRoot = null;

    public GuiBase(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    public void buildLayout() {
        layoutRoot = new LayoutSpacer();
    }

    public ElementBase addVisible(ElementBase element) {
        elements.add(element);
        return element;
    }

    /**
     * If elements need to be added/removed, call this to rebuild the whole layout tree and elements list.
     */
    protected void rebuild() {
        layoutRoot = null;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        rebuild();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        if (layoutRoot == null) {
            elements.clear();
            buildLayout();

            // track interactive elements
            buttonList.clear();
            textFieldList.clear();
            labelList.clear();
            for (ElementBase element : elements) {
                if (element.getButton() != null) {
                    buttonList.add(element.getButton());
                }

                if (element.getTextField() != null) {
                    textFieldList.add(element.getTextField());
                }

                // don't need to track labels, since they're not interactive
            }
        }

        final Vec2 newSize = new Vec2(width, height);
        if (!newSize.equals(layoutRoot.getCurrentSize())) {
            layoutRoot.setSize(newSize);
            layoutRoot.setCoords(new Vec2(0, 0));
        }

        for (ElementBase element : elements) {
            element.draw(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (!button.enabled) return;

        for (ElementBase element : elements) {
            if (element.getId() == button.id) {
                element.onButtonClicked();
                break;
            }
        }
    }

    @Override
    public void keyTyped(char keyChar, int keyCode) {
        for (GuiTextField textField : textFieldList) {
            if (textField.isFocused()) {
                textField.textboxKeyTyped(keyChar, keyCode);
            }
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) throws IOException {
        super.mouseClicked(x, y, mouseButton);
        for (GuiTextField textField : textFieldList) {
            textField.mouseClicked(x, y, mouseButton);
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
