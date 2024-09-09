import io.socket.emitter.Emitter;
import jsclub.codefest2024.sdk.Hero;
import jsclub.codefest2024.sdk.algorithm.PathUtils;
import jsclub.codefest2024.sdk.base.Node;
import jsclub.codefest2024.sdk.model.GameMap;
import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
import jsclub.codefest2024.sdk.model.players.Player;
import jsclub.codefest2024.sdk.model.weapon.Weapon;
import jsclub.codefest2024.sdk.model.Element;

import java.io.IOException;
import java.sql.Array;
import java.util.*;


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
    // CALCULATE DISTANCE BETWEEN TWO OBJECT
    public static double calDistance(Node x, Node y){
        return Math.sqrt(Math.pow(x.getX()-y.getX(),2) + Math.pow(x.getY()-y.getY(),2));
    }


    // PUT THE CLOSEST ELEMENT TO THE MAIN PLAYER ON THE TOP OF THE LIST
    public static <T extends Element> List <T> sortElement(List<T> elementList, Player player){

        Comparator <T> compare = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if(Action.calDistance(player,o1) < Action.calDistance(player,o2))
                    return -1;
                else if(Action.calDistance(player,o1) < Action.calDistance(player,o2))
                    return 0;
                else
                    return 1;
            }
        };

        elementList.sort(compare);
        return elementList;
    }
}
