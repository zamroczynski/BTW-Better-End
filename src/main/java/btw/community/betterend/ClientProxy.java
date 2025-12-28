package btw.community.betterend;

import btw.community.betterend.client.RenderEnderMite;
import btw.community.betterend.entity.EntityEnderMite;
import net.minecraft.src.RenderManager;
import java.lang.reflect.Field;
import java.util.Map;

public class ClientProxy {
    public static void registerRenderers() {
        try {
            RenderEnderMite renderer = new RenderEnderMite();

            renderer.setRenderManager(RenderManager.instance);

            Field renderMapField = RenderManager.class.getDeclaredField("entityRenderMap");
            renderMapField.setAccessible(true);

            Map<Class, Object> renderMap = (Map<Class, Object>) renderMapField.get(RenderManager.instance);
            renderMap.put(EntityEnderMite.class, renderer);
        } catch (Exception e) {
            System.err.println("BetterEnd Error: Failed to register renderers!");
            e.printStackTrace();
        }
    }
}