import jsclub.codefest2024.sdk.base.Node;
import jsclub.codefest2024.sdk.model.enemies.Enemy;
import jsclub.codefest2024.sdk.model.equipments.Armor;
import jsclub.codefest2024.sdk.model.equipments.HealingItem;
import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
import jsclub.codefest2024.sdk.model.players.Player;
import jsclub.codefest2024.sdk.model.weapon.Bullet;
import jsclub.codefest2024.sdk.model.weapon.Weapon;

import java.util.Comparator;
import java.util.List;

public class SubClass {
    private int countHead = 0;
    private boolean isHeading = false;
    private boolean isShooting = false;

    public SubClass(){
    }

    public int getCountHead() {
        return countHead;
    }

    public void setCountHead() {
        this.countHead = countHead + 1;
    }

    public static boolean equal(Node a, Node b){
        return a.getX()==b.getX() && a.getY()==b.getY();
    }

    public static int calDistance(Node a, Node b){
        return Math.abs(a.getX()-b.getX())+Math.abs(a.getY()-b.getY());
    }

//    public static Node getMinFromSortNodeList(List<Node> nodeList, Player player){
//        nodeList.sort(new Comparator<Node>() {
//            @Override
//            public int compare(Node o1, Node o2) {
//                if(calDistance(player, o1) < calDistance(player, o2)){
//                    return -1;
//                }else if(calDistance(player, o1) >= calDistance(player, o2)){
//                    return 1;
//                }
//                return 0;
//            }
//        });
//        return nodeList.getFirst();
//    }

    public static List<Obstacle> sortObstacle(List<Obstacle> obstacleList, Node currentPos){
        obstacleList.sort(new Comparator<Obstacle>() {
            @Override
            public int compare(Obstacle o1, Obstacle o2) {
                return calDistance(currentPos,o1) - calDistance(currentPos, o2);
            }
        });
        return obstacleList;
    }

    public static List<Weapon> sortWeapon(List<Weapon> weaponList, Node currentPos){
        weaponList.sort(new Comparator<Weapon>() {
            @Override
            public int compare(Weapon o1, Weapon o2) {
                return calDistance(currentPos,o1) - calDistance(currentPos, o2);
            }
        });
        return weaponList;
    }

    public static List<Player> sortEnemy(List<Player> enemyList, Player player){
        enemyList.sort(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return calDistance(player, o1)-calDistance(player, o2);
            }
        });
        return enemyList;
    }

    public static List<Enemy> sortNPC(List<Enemy> enemyList, Player player){
        enemyList.sort(new Comparator<Enemy>() {
            @Override
            public int compare(Enemy o1, Enemy o2) {
                return calDistance(player, o1)-calDistance(player, o2);
            }
        });
        return enemyList;
    }

    public static List<Armor> sortArmor(List<Armor> armorList, Player player){
        armorList.sort(new Comparator<Armor>() {
            @Override
            public int compare(Armor o1, Armor o2) {
                if(calDistance(player, o1) < calDistance(player, o2)){
                    return -1;
                }else if(calDistance(player, o1) >= calDistance(player, o2)){
                    return 1;
                }
                return 0;
            }
        });
        return armorList;
    }
    public static List<HealingItem> sortHealingItem(List<HealingItem> healingItemsList, Player player){
        healingItemsList.sort(new Comparator<HealingItem>() {
            @Override
            public int compare(HealingItem o1, HealingItem o2) {
                if(calDistance(player, o1) < calDistance(player, o2)){
                    return -1;
                }else if(calDistance(player, o1) >= calDistance(player, o2)){
                    return 1;
                }
                return 0;
            }
        });
        return healingItemsList;
    }

    public boolean isHeading() {
        return isHeading;
    }

    public void setHeading(boolean heading) {
        isHeading = heading;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean shooting) {
        isShooting = shooting;
    }
}