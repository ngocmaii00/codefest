import io.socket.emitter.Emitter;
import jsclub.codefest2024.sdk.algorithm.PathUtils;
import jsclub.codefest2024.sdk.base.Node;
import jsclub.codefest2024.sdk.model.GameMap;
import jsclub.codefest2024.sdk.Hero;
import jsclub.codefest2024.sdk.model.enemies.Enemy;
import jsclub.codefest2024.sdk.model.equipments.Armor;
import jsclub.codefest2024.sdk.model.equipments.HealingItem;
import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
import jsclub.codefest2024.sdk.model.players.Player;
import jsclub.codefest2024.sdk.model.weapon.Weapon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    //    private static final String SERVER_URL = "http://192.168.50.20";
    private static final String SERVER_URL = "https://cf-server.jsclub.dev";
    private static final String GAME_ID = "156536";
    private static final String PLAYER_NAME = "test-Himawari";
    private static final String PLAYER_KEY = "b5e4efee-dbaa-4f12-986e-d009fb0eef5b";


    public static void main(String[] args) throws IOException {
        Hero hero = new Hero(GAME_ID, PLAYER_NAME, PLAYER_KEY);
        SubClass subClass = new SubClass();

        Emitter.Listener onMapUpdate = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    GameMap gameMap = hero.getGameMap();
                    gameMap.updateOnUpdateMap(args[0]);

                    Player me = gameMap.getCurrentPlayer();

                    List<Player> otherPlayers = gameMap.getOtherPlayerInfo();

                    List<Obstacle> restrictedList = new ArrayList<>();
                    List<Obstacle> chestList = SubClass.sortObstacle(gameMap.getListChests(), me);

                    Enemy closestNPC = SubClass.sortNPC(gameMap.getListEnemies(), me).getFirst();
                    System.out.println("closest npc: "+closestNPC.getId());

                    restrictedList.addAll(chestList);
                    restrictedList.addAll(gameMap.getListTraps());
                    restrictedList.addAll(gameMap.getListIndestructibleObstacles());

                    List<Node> avoidNodeList = new ArrayList<>();

                    for (Obstacle o:restrictedList) {
                        avoidNodeList.add(new Node(o.getX(), o.getY()));
                    }

                    for (int i = 0; i<=2; i++){
                        for (int j = 0; j<=2; j++){
                            avoidNodeList.add(new Node(closestNPC.getX()+i, closestNPC.getY()+j));
                        }
                    }

                    Weapon currentGun = hero.getInventory().getGun();
                    Weapon currentMelee = hero.getInventory().getMelee();
                    Weapon currentThrowable = hero.getInventory().getThrowable();
                    List<HealingItem> currentHealingItems = hero.getInventory().getListHealingItem();
                    List<Armor> currentArmorList = hero.getInventory().getListArmor();

                    if (currentGun != null) {
                        System.out.println("gun:" + currentGun.getId());
                    }
                    if (currentMelee != null) {
                        System.out.println("melee:" + currentMelee.getId());
                    }
                    if (currentThrowable != null) {
                        System.out.println("throwable:" + currentThrowable.getId());
                    }

                    //find chest
                    Node targetChest = new Node();
                    if (currentMelee.getId().compareTo("HAND")==0 || currentThrowable==null || currentArmorList.size()<2 || currentHealingItems.size()<4) {
                        while (chestList.getFirst() != null && !PathUtils.checkInsideSafeArea(chestList.getFirst(), gameMap.getDarkAreaSize() + 4, gameMap.getMapSize())) {
                            chestList.removeFirst();
                        }
                        if (chestList.getFirst() != null) {
                            targetChest = new Node(chestList.getFirst().getX() + 1, chestList.getFirst().getY());
                        }
                        System.out.println("chest:" + targetChest.getX() + " " + targetChest.getY());
                    }else {
                        targetChest = null;
                    }


                    //find enemy
                    final List<Player> enemies = SubClass.sortEnemy(otherPlayers, me);
                    int i = 0;
                    Player currentEnemy = enemies.get(i);
                    while ((!currentEnemy.getIsAlive() || !PathUtils.checkInsideSafeArea(currentEnemy, gameMap.getDarkAreaSize() + 4, gameMap.getMapSize())
                            || PathUtils.getShortestPath(gameMap, avoidNodeList, me, currentEnemy, false) == null)) {
                        if (i==enemies.size()-1){
                            break;
                        }
                        i++;
                        currentEnemy = enemies.get(i);
                    }

                    Node target = new Node(currentEnemy.getX(), currentEnemy.getY());
                    int dx = me.getX() - target.getX();
                    int dy = me.getY() - target.getY();

                    //find healing item
                    List<HealingItem> healingItemList = SubClass.sortHealingItem(gameMap.getListHealingItems(), me);
                    Node targetHealing = new Node();
                    if (!healingItemList.isEmpty()) {
                        int healingIndex = 0;
                        while (!PathUtils.checkInsideSafeArea(healingItemList.get(healingIndex), gameMap.getDarkAreaSize() + 4, gameMap.getMapSize())
                                || PathUtils.getShortestPath(gameMap, avoidNodeList, me, healingItemList.get(healingIndex), false)==null){
                            if (healingIndex == healingItemList.size()-1){
                                break;
                            }
                            healingIndex++;
                        }
                        if (PathUtils.getShortestPath(gameMap, avoidNodeList, me, healingItemList.get(healingIndex), false)!=null) {
                            targetHealing = healingItemList.get(healingIndex);
                            System.out.println("healing item");
                        }
                    }

                    //find armorItem
                    List<Armor> armorList = SubClass.sortArmor(gameMap.getListArmors(), me);
                    Node targetArmor = new Node();
                    if (!armorList.isEmpty()) {
                        int armorIndex = 0;
                        while (!PathUtils.checkInsideSafeArea(armorList.get(armorIndex), gameMap.getDarkAreaSize() + 3, gameMap.getMapSize()) ||
                                PathUtils.getShortestPath(gameMap, avoidNodeList, me, armorList.get(armorIndex), false)==null){
                            if (armorIndex == armorList.size()-1){
                                break;
                            }
                            armorIndex++;
                        }
                        if (PathUtils.getShortestPath(gameMap, avoidNodeList, me, armorList.get(armorIndex), false)!=null) {
                            targetArmor = armorList.get(armorIndex);
                            System.out.println("armor");
                        }
                    }

                    //find weapon
                    List<Weapon> weaponList = SubClass.sortWeapon(gameMap.getListWeapons(), me);
                    Weapon targetWeapon = weaponList.getFirst();
                    if (currentGun==null || currentThrowable==null || currentMelee.getId().compareTo("HAND")==0) {
                        while ((weaponList.getFirst() != null)
                                && (!PathUtils.checkInsideSafeArea(targetWeapon, gameMap.getDarkAreaSize()+4, gameMap.getMapSize())
                                || (currentGun != null && targetWeapon.getType().toString().compareTo("GUN") == 0)
                                || (currentMelee.getId().compareTo("HAND")!=0 && targetWeapon.getType().toString().compareTo("MELEE") == 0)
                                || (currentThrowable != null && targetWeapon.getType().toString().compareTo("THROWABLE") == 0))) {
                            weaponList.removeFirst();
                            System.out.println("Skip weapon");
                            if (weaponList.isEmpty()){
                                targetWeapon = null;
                                break;
                            }
                            targetWeapon = weaponList.getFirst();
                        }
                        if (targetWeapon!=null){
                            System.out.println("target weapon: "+targetWeapon.getId());
                        }
                    } else {
                        targetWeapon = null;
                    }

                    boolean checkMoved = false;

                    int choice;
                    if (SubClass.calDistance(me, target)<=4) {
                        choice = 2; //shoot and attack
                    }else if (SubClass.calDistance(me, target)<=8 && (currentMelee.getId().compareTo("HAND")!=0 || currentGun!=null)){
                        choice = 2;
                    }else if (currentGun==null || currentThrowable == null || currentMelee.getId().compareTo("HAND")==0){
                        choice = 1; //go to find any weapon
                    } else{
                        choice = 2; //shoot and attack
                    }
                    switch (choice){
                        case 1:{
                            //pick armor
                            if (targetArmor!=null && currentArmorList.size() < 2 && SubClass.calDistance(targetArmor, me) <= 6){
                                if (SubClass.equal(me, targetArmor)){
                                    if ((gameMap.getElementByIndex(targetArmor.getX(), targetArmor.getY()).getId().compareTo("POT")==0
                                            || gameMap.getElementByIndex(targetArmor.getX(), targetArmor.getY()).getId().compareTo("HELMET")==0)
                                            && subClass.getCountHead()<=1){
                                        hero.pickupItem();
                                        subClass.setCountHead();
                                    }else {
                                        hero.pickupItem();
                                        System.out.println("pick armor");
                                    }
                                } else {
                                    String path = PathUtils.getShortestPath(gameMap, avoidNodeList, me, targetArmor, false);
                                    hero.move(path); checkMoved = true;
                                } //pick healing
                            } else if (targetHealing!=null && currentHealingItems.size() < 4 && SubClass.calDistance(targetHealing, me) <= 6){
                                if (SubClass.equal(me, targetHealing)){
                                    hero.pickupItem();
                                    System.out.println("pick heal");
                                }else{
                                    String path = PathUtils.getShortestPath(gameMap, avoidNodeList, me, targetHealing, false);
                                    hero.move(path); checkMoved = true;
                                }
                            }

                            if (!checkMoved) {
                                if (targetWeapon!=null && targetChest!=null && SubClass.calDistance(me, targetWeapon) <= SubClass.calDistance(me, targetChest)) {
                                    if (SubClass.equal(me, targetWeapon)){
                                        System.out.println("pick weapon");
                                        hero.pickupItem();
                                    }else {
                                        System.out.println("move to weapon");
                                        hero.move(PathUtils.getShortestPath(gameMap, avoidNodeList, me, targetWeapon, false));
                                        checkMoved = true;
                                    }
                                } else if (currentGun==null && targetWeapon!=null && targetChest==null) {
                                    if (SubClass.equal(me, targetWeapon)){
                                        System.out.println("pick weapon");
                                        hero.pickupItem();
                                    }else {
                                        System.out.println("move to weapon");
                                        hero.move(PathUtils.getShortestPath(gameMap, avoidNodeList, me, targetWeapon, false));
                                        checkMoved = true;
                                    }
                                } else if (targetChest!=null && SubClass.calDistance(me, target)>4){
                                    if (SubClass.equal(me, targetChest)){
                                        System.out.println("attack chest");
                                        hero.attack("l");
                                    }else {
                                        hero.move(PathUtils.getShortestPath(gameMap, avoidNodeList, me, targetChest, false));
                                    }
                                    checkMoved = true;
                                }
                            }
                        }
                        case 2:{
                            if(currentThrowable != null && ((Math.abs(dx) <= 2 && Math.abs(dy) <= 8 && Math.abs(dy) >= 4) || (Math.abs(dy) <= 2 && Math.abs(dx) <= 8 && Math.abs(dx) >= 4))){
                                if (Math.abs(dx) <= 1){
                                    if (dy < 0){
                                        hero.throwItem("u");
                                    }else if(dy > 0){
                                        hero.throwItem("d");
                                    }
                                }else if (Math.abs(dy) <= 1){
                                    if (dx < 0){
                                        hero.throwItem("r");
                                    }else {
                                        hero.throwItem("l");
                                    }
                                }
                            } else if (currentGun == null && SubClass.calDistance(me, target)==1){
                                if (dy == 0) {
                                    if (dx > 0) {
                                        hero.attack("l");
                                    } else if (dx < 0) {
                                        hero.attack("r");
                                    }
                                } else {
                                    if (dy > 0) {
                                        hero.attack("d");
                                    } else {
                                        hero.attack("u");
                                    }
                                }
                            } else if (currentGun == null && SubClass.calDistance(me, target)<=4){
                                hero.move(PathUtils.getShortestPath(gameMap, avoidNodeList, me, target, false));
                                checkMoved = true;
                            }else if (currentGun!=null && SubClass.calDistance(me, target)<=4){
                                if (SubClass.calDistance(me, target)==1){
                                    if (!subClass.isShooting()){
                                        if (dy == 0) {
                                            if (dx > 0) {
                                                hero.attack("l");
                                            } else if (dx < 0) {
                                                hero.attack("r");
                                            }
                                        } else {
                                            if (dy > 0) {
                                                hero.attack("d");
                                            } else {
                                                hero.attack("u");
                                            }
                                        }
                                        subClass.setShooting(true);
                                    }else{
                                        if (dy == 0) {
                                            if (dx > 0) {
                                                hero.shoot("l");
                                            } else if (dx < 0) {
                                                hero.shoot("r");
                                            }
                                        } else {
                                            if (dy > 0) {
                                                hero.shoot("d");
                                            } else {
                                                hero.shoot("u");
                                            }
                                        }
                                        subClass.setShooting(false);
                                    }
                                } else if ((dx == 0 && Math.abs(dy) <= 4 || (dy == 0 && Math.abs(dx) <= 4))) {
                                    if (!subClass.isHeading()) {
                                            if (dy == 0) {
                                                if (dx > 0) {
                                                    hero.shoot("l");
                                                } else if (dx < 0) {
                                                    hero.shoot("r");
                                                }
                                            } else {
                                                if (dy > 0) {
                                                    hero.shoot("d");
                                                } else {
                                                    hero.shoot("u");
                                                }

                                            }

                                        subClass.setHeading(true);
                                    }else {
                                        if (!checkMoved){
                                            hero.move(PathUtils.getShortestPath(gameMap, avoidNodeList, me, target, false));
                                            checkMoved = true;
                                            subClass.setHeading(false);
                                        }
                                    }
                                }
                            }
                            if (!checkMoved){
                                hero.move(PathUtils.getShortestPath(gameMap, avoidNodeList, me, target, false));
                                System.out.println("move to enemy");
                            }
                        }
                        default:{
                            if (!currentHealingItems.isEmpty() && SubClass.calDistance(me, currentEnemy) > 8 && me.getHp()<=90){
                                hero.useItem(currentHealingItems.getFirst().getId());
                                System.out.println("use heal");
                            }

                            if (currentArmorList.size() < 2 && targetArmor!=null && me.getX()==targetArmor.getX() && me.getY()==targetArmor.getY()){
                                if ((gameMap.getElementByIndex(targetArmor.getX(), targetArmor.getY()).getId().compareTo("POT")==0
                                        || gameMap.getElementByIndex(targetArmor.getX(), targetArmor.getY()).getId().compareTo("HELMET")==0)
                                        && subClass.getCountHead()<=1){
                                    hero.pickupItem();
                                    subClass.setCountHead();
                                }else {
                                    hero.pickupItem();
                                }
                            }

                            if (currentHealingItems.size() < 4 && targetHealing!=null && me.getX()==targetHealing.getX() && me.getY()==targetHealing.getY()){
                                hero.pickupItem();
                            }
                        }
                    }

                }catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        };


        hero.setOnMapUpdate(onMapUpdate);
        hero.start(SERVER_URL);
    }
}
