package btw.community.betterend;

import btw.community.betterend.client.RenderEnderMite;
import btw.community.betterend.entity.EntityEnderMite;
import btw.community.betterend.client.RenderEnderGuardian;
import btw.community.betterend.entity.EntityEnderGuardian;
import net.minecraft.src.Render;
import net.minecraft.src.RenderManager;
import java.lang.reflect.Field;
import java.util.Map;

public class ClientProxy {
    public static void registerRenderers() {
        try {
            RenderEnderMite renderer = new RenderEnderMite();
            renderer.setRenderManager(RenderManager.instance);
            Field renderMapField = null;
            for (Field field : RenderManager.class.getDeclaredFields()) {
                if (Map.class.isAssignableFrom(field.getType())) {
                    renderMapField = field;
                    break;
                }
            }

            if (renderMapField == null) {
                throw new RuntimeException("BetterEnd Error: Could not find entityRenderMap field in RenderManager!");
            }

            renderMapField.setAccessible(true);
            Map<Class, Render> renderMap = (Map<Class, Render>) renderMapField.get(RenderManager.instance);
            renderMap.put(EntityEnderMite.class, renderer);
            RenderEnderGuardian guardianRenderer = new RenderEnderGuardian();
            guardianRenderer.setRenderManager(RenderManager.instance);
            renderMap.put(EntityEnderGuardian.class, guardianRenderer);
        } catch (Exception e) {
            System.err.println("BetterEnd Error: Failed to register renderers!");
            e.printStackTrace();
        }
    }
}