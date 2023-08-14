package com.connexal.ravelcraft.mod.server.managers;

import com.connexal.ravelcraft.mod.server.util.SkullUtils;
import com.connexal.ravelcraft.shared.util.ChatColor;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MiniBlockManager {
    private final List<ItemStack> miniBlocks = new ArrayList<>();

    public MiniBlockManager() {
        this.addMiniBlock("Oak Planks", "1a0271812019405a9ca9fb6afa76b171e1cec691e24b6f0dc50042f363c0b660");
        this.addMiniBlock("Oak Log", "a6d292a6e89526a469a85153856ef7d5b0189b66f06bae94973b2274dcdf31a5");

        this.addMiniBlock("Burger", "efd014113c45b9791917c24dc0c0a304e80f1a5674130bb51724dc13030e40b5");
        this.addMiniBlock("Fries", "806db375e81c063747f9718e725b05cb37b16f8aec5c7f21fd537de62540ecc9");
        this.addMiniBlock("Donut", "3ab04847f09b3f2e11fc412c2e241accf99675432933b471681005c42e5d2285");
        this.addMiniBlock("Sushi", "e5347dadf68199fa7a1b66f0481ad8e9daee1510865add6f33d15fb378d13e91");
        this.addMiniBlock("Cake", "6a260ce62460be989257fde905d76920499a263347d44e7d1c662794fa2d6d33");
        this.addMiniBlock("Tropical Fruit", "2131ce98243047fc81e45eb9ef4bf63829cffd005d223137fc09111ba4aad05a");
        this.addMiniBlock("Mushroom", "3207e56bf14964b6d504e152191657a403d4f770be4c18f4c400182795938096");
        this.addMiniBlock("Watermelon", "351883d6b7fa42dd0cf0d9cd999615a8540bc07c6fcd5be5dbea5617fd139fd4");
        this.addMiniBlock("Melon", "baae29785982bf80c13c4d68fafbc267a667d013ed9fecb4aa9a3a741fae4383");
        this.addMiniBlock("Lemon", "4378b582d19ccc55b023eb82eda271bac4744fa2006cf5e190246e2b4d5d");
        this.addMiniBlock("Apple", "35e2e0959712dcd3357cc3cea85f99b3fd8097855c754b9b171f963514225d");
        this.addMiniBlock("Rotten Apple", "3183a8fa649c7f58a5d57d5fdfba68ae172b11362b72f508afde6e3461b0d029");
        this.addMiniBlock("Bread", "632545a9897c4f0809d7e9d7de4b499cafd8d6a42a0eacf4caabf3488adca211");
        this.addMiniBlock("Peacan Pie", "ad3b76df3aa76893a161895d981f2248fcde96dbc4042129045f95de10e18485");
        this.addMiniBlock("Turkey", "abae9edc6a8cb0ca28dfff435fe5ac44ccd06810d5685c9938408b124f378819");
        this.addMiniBlock("Bottle of Whiskey", "c4527d2e2e57f957e2951cf95d607632248471fb7e26597e8075f89d8d987474");
        this.addMiniBlock("Bottle of Wine", "ac1b17cc6c9c760d78619253ba4f6ccff41524aec105b1d2641a74f356967831");
        this.addMiniBlock("Beer", "5ea7858ddb53f3fc13be9d7147df408ff7f843dd21af19c39b3dd58b5241695d");
        this.addMiniBlock("Flour", "9a4181c1a4c45ef611406c7dd68a4dc4831218087dae19a62a4decb2dcdd35c0");
        this.addMiniBlock("Red Jam Jar", "c0b8b5889ee1c6388dc6c2c5dbd70b6984aefe54319a095e64db7638097b821");
        this.addMiniBlock("Blue Jam Jar", "91708ed352e17ca89c1c9485cd1db017c4c886895ab5c7c27a9ff564af2172d");
        this.addMiniBlock("Bag of Sugar", "53ce76602d3fec7c0273da60009007be4140ac9fac0341450c0757e53d751577");
        this.addMiniBlock("Apple Basket", "476d61d0da9c3395725fd50da17896219379fc99cdb5f6137beacf49f237e2ef");
        this.addMiniBlock("Carrot Basket", "8a2338913e608a39e2006f646a1e7c86710accbe381e64e22678f3bfa9231b9b");
        this.addMiniBlock("Food Crate", "1591b61529d25a7ecd6bec00948e6fe155e3007f2d7fe559f3a83c6f808e434d");
        this.addMiniBlock("Tomato", "72df4e674951c138e7311127561fdbd27e2150716b02bb568747f8545fb20145");
        this.addMiniBlock("Cauliflower", "14a6dedd99bb9af3f1b2f338d509a926606cddfdc351e018aad1c07015ad566d");
        this.addMiniBlock("Grapes", "ee5935863c53a996f5334e90f55de538e83ffc5f6b0b8e83a4dc4f6e6b1208");

        this.addMiniBlock("Barrel Sideways", "c7502c6a68852ebf1087a2ed2a63f8d85898a7049d514b2c675fa0915bf47246");
        this.addMiniBlock("Barrel Upright", "bae751770a70a008a03e1397cbc5be8ae3f782986a814f04943cf62a7121bc3f");
        this.addMiniBlock("Computer", "c1bfa75780a8b1a20994076411789d85fd89d77180c4d143d44c9722b0526675");
        this.addMiniBlock("Books", "eee8d6f5cb7a35a4ddbda46f0478915dd9ebbcef924eb8ca288e91d19c8cb");
        this.addMiniBlock("Old Books", "e344d83e7c6ece90d34c685a9a3384f8e8d0151633fc2eeae4d4b636862d33");
        this.addMiniBlock("Enchanted Books", "b62651879d870499da50e34036800ddffd52f3e4e1993c5fc0fc825d03446d8b");
        this.addMiniBlock("Nintendo Switch Upright", "289f7c4bdb36334fd4f940df7503ab00afd612ea4427aa22523b9b32af857a4");
        this.addMiniBlock("Nintendo Switch Sideways", "7c684155786110896b4291f8219c8365dac352ec6766c0be09aa88d36618ab43");
        this.addMiniBlock("Chest", "c01790010a745d7e48694ff5138996e49728b88a0da32e53c3e6c1fa5d0a806f");
        this.addMiniBlock("Present", "e32a89b6f422b0a394d1ecb414d7ec13be1d107526b3bd0261135beafad70261");
        this.addMiniBlock("Globe", "44c564960774d4a775fc3f1a7696e74f8caf1a204d495d3fb1abb04fa91d2bf");
        this.addMiniBlock("Clock", "8477dafc8c9ea0799633381793866d146c9a39fad4c6684e7117d97e9b6c3");
        this.addMiniBlock("Camera", "24c8ffbbaf52f59171f9445f68485cf7e7e1d6a9087ee7ef9b3869a7211779f");
        this.addMiniBlock("Surveillance Camera", "2ae3a3a4a1aa50d85dbcdac8da63d7cbfd45e520dfec2d50bedf8e90e8b0e4ea");
        this.addMiniBlock("Water Bucket", "54421782fa9e041ca8cce6aac3a0b337ecc1b971b168ecbeef68b8813ee396db");
        this.addMiniBlock("Bucket", "117d86e3f81cc1837c70f10be4889834c04f57a7e98e0dd0b4b223b50c7a8f90");
        this.addMiniBlock("Cup 1", "a3d3d31da27292204c3c4722ad8753f7148d0a499bd688b22f599fe55010371a");
        this.addMiniBlock("Cup 2", "65171f741afbab99022556ca6c288f447a29d9f3be9733395d8a16072dc9571d");
        this.addMiniBlock("Pot", "f38064cd144de50df9b6d0d55998eda25e6bc97bd091af742a98511f2d3ab22");
        this.addMiniBlock("Pan", "6efb58d456024d2d92d9ef59a37e0112ab7349091961f3eab4f7af132de7eecb");
        this.addMiniBlock("Bird House", "97f82aceb98fe069e8c166ced00242a76660bbe07091c92cdde54c6ed10dcff9");
        this.addMiniBlock("Mailbox", "1182476b5efb7d6f733966f4fcbe4bc314637120b87a46d776efffcd0f8b2655");
        this.addMiniBlock("Green Potion", "5b66dcce3142cf2ba0ba29ec29e71160723f5eb598793a7312d324af3ab98");
        this.addMiniBlock("Blue Potion", "40c6c3c95337365799c6c502d6fc34a5b1975cf7364ba2de8fdb0af51e6a28c");

        this.addMiniBlock("Slime Block", "4934a9f5ab1789a7d8dd96d32493cdacff577d8c81e7b23917dff2e32bd0bc10");
        this.addMiniBlock("Honey Block", "4a7e0d55ea8051569955da138697174a0bd76190b2d58b997509737dce5fb61f");

        this.addMiniBlock("Cactus", "d76d448e1269ec841e48764b8fc06b796279fddf8eb7d3c76b019d4a6f1bac69");
        this.addMiniBlock("Azalea Leaves", "9ed51ba8262cd2a66afdb33879e663115469cba4ba7a662b3240ddf0474ba13f");
        this.addMiniBlock("Strawberry Kiwifruit", "f97fcf14393c0dcc2c5b48ef8f25152b7da2af02fc744fc4396eb4449f424248");
        this.addMiniBlock("Mango", "80ee816121d114e1399ab9b37c460ae36dddf251fb1a9b7257f1acce3ff7852d");
        this.addMiniBlock("Carved Pumpkin", "d5112e8226ffd3b34c41d5f087d9584a8768fe72e85d70e43c721075258ed242");
        this.addMiniBlock("Pumpkin", "49f6f7b9605dff328758475181cf3250af1b0ec679f89c5c5f9236b645c2550");

        this.addMiniBlock("Iron Ore", "101843ec43f088c963ffc3e2f71c66e3155943b177a1a35982b120f6f64822bc");
        this.addMiniBlock("Gold Ore", "73bc965d579c3c6039f0a17eb7c2e6faf538c7a5de8e60ec7a719360d0a857a9");
        this.addMiniBlock("Diamond Ore", "e8baf7d4d835c99b8cf060e0b3e929f8918b6482844b1cba0781aa0cfa5acb71");
        this.addMiniBlock("Emerald Ore", "4053851527c4c9ef30a61fb067ebce957c726e1687f8b530fb4a6beeba438bd");
        this.addMiniBlock("Copper Ore", "5184f4d5eeea9bfd1657b7669b6ea8a24329ff6d973b7bdf388fca562a8813f5");
        this.addMiniBlock("Ancient Debris", "9fa2610545c5193b1776fae6f5d6f17579d6002aea032f9f52b54bd3bff59a51");

        this.addMiniBlock("Iron Block", "61a3e0971a78818e05b54ca070f9a7ed8dfc0ef7269078da0aff4575499887b1");
        this.addMiniBlock("Gold Block", "54bf893fc6defad218f7836efefbe636f1c2cc1bb650c82fccd99f2c1ee6");
        this.addMiniBlock("Diamond Block", "9631597dce4e4051e8d5a543641966ab54fbf25a0ed6047f11e6140d88bf48f");
        this.addMiniBlock("Emerald Block", "bc0e6d9e242735481918c5fd14498bd760bb9f4ff6430ad4696b38e8a883da97");
        this.addMiniBlock("Copper Block", "40a6e6aa7ce99c81daf816d69638d52b2882a2558880222e9a466dcedc88015a");
        this.addMiniBlock("Amethyst Block", "3f4876b6a5d6dd785e091fd134a21c91d0a9cac5a622e448b5ffcb65ef45278");
        this.addMiniBlock("Amethyst Gem", "a79f8c92776d642d119f9e92360b1e5b971e66e61428a3e1b311d8b6185e6");
        this.addMiniBlock("Netherite Block", "2980fe762f48861397f0c8dc2f8d13f7c166170038e973020689192a89308f3d");

        this.addMiniBlock("Shark", "31636ae6e558cf835e7a43bcfd0bd4a277d56d66a5f4c8bcc0a03ed6632510");
        this.addMiniBlock("Cow", "d6551840955f524367580f11b35228938b6786397a8f2e8c8cc6b0eb01b5db3d");
        this.addMiniBlock("Cool Cow", "1da9216dca905e0f59a17cb1cad96a2a1966ba9d9ed435e8cdfde8cf8dca4004");
        this.addMiniBlock("Cool Mooshroom", "7f3130468ac480a427db13ad13e8ca8526b538ab7ad7d69fc9952f3c53aea8d1");
        this.addMiniBlock("Green Parrot", "5fc9a3b9d5879c2150984dbfe588cc2e61fb1de1e60fd2a469f69dd4b6f6a993");
        this.addMiniBlock("Blue Parrot", "20e03b10c15ee5601423867dfb8bcbcbc919ca96c0eea63073ec8e795eabd05f");
        this.addMiniBlock("Cyan Parrot", "bc6471f23547b2dbdf60347ea128f8eb2baa6a79b0401724f23bd4e2564a2b61");
        this.addMiniBlock("Red Parrot", "5d1a168bc72cb314f7c86feef9d9bc7612365244ce67f0a104fce04203430c1d");
        this.addMiniBlock("Pig", "bee8514892f3d78a32e8456fcbb8c6081e21b246d82f398bd969fec19d3c27b3");
        this.addMiniBlock("Muddy Pig", "fdb851cfdeb864e8c59019c26121ea0190da097255d91ef626b60cab3b079bc5");
        this.addMiniBlock("Pufferfish", "292350c9f0993ed54db2c7113936325683ffc20104a9b622aa457d37e708d931");
        this.addMiniBlock("Turkey", "b7fcca58d6996621b2a66becd1b8d4d1b1901ed1f96b09ada2406de9765fb097");
        this.addMiniBlock("Chicken", "ca3582ce4889333dad329e4e24372a03a5daa2c34280c56256af5283edb043f8");
        this.addMiniBlock("Chick", "702b27a2e6eff84b043d68c3c3ea6647bef1fa076395cec0898a619673a116f9");
        this.addMiniBlock("Bird", "ebee642cb4b4fb6671dec9ab9349f0c1ca83bc152738956c1efa2ff2c1030960");
        this.addMiniBlock("Allay", "e50294a1747310f104124c6373cc639b712baa57b7d926297b645188b7bb9ab9");
        this.addMiniBlock("Squid", "464bdc6f600656511bef596c1a16aab1d3f5dbaae8bee19d5c04de0db21ce92c");
        this.addMiniBlock("White Sheep", "292df216ecd27624ac771bacfbfe006e1ed84a79e9270be0f88e9c8791d1ece4");
        this.addMiniBlock("Rabbit", "cc4349fe9902dd76c1361f8d6a1f79bff6f433f3b7b18a47058f0aa16b9053f");
        this.addMiniBlock("Cold Frog", "ce62e8a048d040eb0533ba26a866cd9c2d0928c931c50b4482ac3a3261fab6f0");
        this.addMiniBlock("Temperate Frog", "23ce6f9998ed2da757d1e6372f04efa20e57dfc17c3a06478657bbdf51c2f2a2");
        this.addMiniBlock("Axolotl", "d704254139a0b1a926e7552482dd67679c6ae0dc8335c980dbd1c0d99634a708");
        this.addMiniBlock("Green Axolotl", "bf6ff988b700f59a28e62c1a1edbc203c0b8a540def6845497c548af72108b1a");
        this.addMiniBlock("Blue Axolotl", "f473a49513b06f3274624025f83324fef4116b5c431084671ded928f59d5a291");
    }

    public List<ItemStack> getMiniBlocks() {
        return this.miniBlocks;
    }

    private void addMiniBlock(String name, String texture) {
        this.miniBlocks.add(SkullUtils.getSkull("http://textures.minecraft.net/texture/" + texture, ChatColor.GREEN + "Mini " + name));
    }
}
