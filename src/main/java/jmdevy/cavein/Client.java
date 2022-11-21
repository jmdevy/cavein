package jmdevy.cavein;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;


// Each client potentially tells the server to start a cave-in
@Mod.EventBusSubscriber(modid = Cavein.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class Client {
    // See the 'processMessage' functions under network/messages/toclient for client behavior dictated by the server
}
