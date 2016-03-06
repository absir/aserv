package G2.Protocol;

import com.baidu.bjf.remoting.protobuf.EnumReadable;

public enum PEntityType implements EnumReadable {

    None(0), EntitySpirit(2), EntityMagic(3), EntityEquip(5), EntityBook(6), EntityItem(7), EntityHorse(8), EntityActor(10), EntityMagicFragment(12), Coin(101), Stamina(102), Qi(103), Exp(104), Dollar(105), Merit(106), ArenaTime(107), GodStamina(108), ArenaScore(109), Zhihui(110), Zhihui2(111), Zhihui3(113);

    private final int value;

    PEntityType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
