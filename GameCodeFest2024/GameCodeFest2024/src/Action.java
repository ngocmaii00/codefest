import io.socket.emitter.Emitter;
import jsclub.codefest2024.sdk.Hero;
import jsclub.codefest2024.sdk.algorithm.PathUtils;
import jsclub.codefest2024.sdk.base.Node;
import jsclub.codefest2024.sdk.model.GameMap;
import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
import jsclub.codefest2024.sdk.model.players.Player;
import jsclub.codefest2024.sdk.model.weapon.Weapon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Action {
    public static String actionAttack(Node a, Node b,Weapon currentWeapon) {
        if (a.getX() == b.x) {

            if (a.getY() <= b.y + currentWeapon.getRange() && a.getY() > b.y)
                return "d";
            else if (a.getY() >= b.y - currentWeapon.getRange() && a.getY() < b.y)
                return "u";

        } else if (a.getY() == b.y) {

            if (a.getX() <= b.x + currentWeapon.getRange() && a.getX() > b.x)
                return "l";
            else if (a.getX() >= b.x - currentWeapon.getRange() && a.getX() < b.x)
                return "r";

        }
        return null;
    }

    public static void getMeleeAndThrowable(Hero hero,Player player, Obstacle chest, Weapon currentGun){
       /* hero.shoot(Action.actionAttack(player, chest, currentGun));
        List<Weapon> meleeList = gameMap.getAllMelee();
        Weapon melee = meleeList.getFirst();
        String movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, melee, false);
        System.out.println(movement);*/
    }
}
