import jsclub.codefest2024.sdk.Hero;
import jsclub.codefest2024.sdk.algorithm.PathUtils;
import jsclub.codefest2024.sdk.base.Node;
import jsclub.codefest2024.sdk.model.Element;
import jsclub.codefest2024.sdk.model.GameMap;
import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
import jsclub.codefest2024.sdk.model.players.Player;
import jsclub.codefest2024.sdk.model.weapon.Weapon;

import java.io.IOException;
import java.util.*;


public class Action {
    public static boolean shot = false;
    public static boolean slashed = false;



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

    public static String evasionAttack(Hero hero,Player a, Player b,Weapon currentWeapon) {
        try {
            if (a.x == b.x) {

                if (a.getY() == b.y + currentWeapon.getRange() && a.getY() > b.y)
                    return "d";
                else if (a.getY() < b.y + currentWeapon.getRange() && a.getY() > b.y) {
                    System.out.println("evasion move up");
                    hero.move("u");
                    return "d";
                }
                if (a.getY() == b.y - currentWeapon.getRange() && a.getY() < b.y)
                    return "u";
                else if (a.getY() > b.y - currentWeapon.getRange() && a.getY() < b.y) {
                    System.out.println("evasion move down");
                    hero.move("d");
                    return "u";
                }

            } else if (a.getY() == b.y) {

                if (a.getX() == b.x + currentWeapon.getRange() && a.getX() > b.x)
                    return "l";
                else if (a.getX() < b.x + currentWeapon.getRange() && a.getX() > b.x){
                    System.out.println("evasion move right");
                    hero.move("r");
                    return "l";
                }if (a.getX() == b.x - currentWeapon.getRange() && a.getX() < b.x)
                        return "r";
                else if(a.getX() > b.x - currentWeapon.getRange() && a.getX() < b.x){
                        System.out.println("evasion move left");
                        hero.move("l");
                        return "r";

                    }

            }
            return null;
        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }public static String evasionAttack(Node a, Node b,Weapon currentWeapon) {
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

    public static <T extends Node> void navigateItem(Hero hero,Player player, T target, List<Obstacle>restrictedList,List<Node>restrictedPoints, GameMap gameMap     ){
        try {
            if(player.getX()==target.getX() && player.getY()==target.getY()){
                hero.pickupItem();
            }else{
                restrictedPoints.addAll(restrictedList);
                hero.move(PathUtils.getShortestPath(gameMap, restrictedPoints, player, target, false));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Element> int getNoNull(GameMap gameMap,Player player, List<T> list,List<Node> restrictedPoints ){
        int itemIndex = 0;
        while (PathUtils.checkInsideSafeArea(list.get(itemIndex),gameMap.getDarkAreaSize(),gameMap.getMapSize())||
                PathUtils.getShortestPath(gameMap,restrictedPoints,player,list.get(itemIndex),true) == null) {
            if(itemIndex == (list.size()-1)){

                break;
            }
            itemIndex++;
        }

        return itemIndex;
    }
}
