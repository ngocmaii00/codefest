import io.socket.emitter.Emitter;
import jsclub.codefest2024.sdk.Hero;
import jsclub.codefest2024.sdk.algorithm.PathUtils;
import jsclub.codefest2024.sdk.base.Node;
import jsclub.codefest2024.sdk.model.GameMap;
import jsclub.codefest2024.sdk.model.equipments.Armor;
import jsclub.codefest2024.sdk.model.equipments.HealingItem;
import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
import jsclub.codefest2024.sdk.model.players.Player;
import jsclub.codefest2024.sdk.model.weapon.Bullet;
import jsclub.codefest2024.sdk.model.weapon.Weapon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static final String SERVER_URL = "https://cf-server.jsclub.dev";
    private static final String GAME_ID = "156536";
    private static final String PLAYER_NAME = "test-N1";
    private static final String PLAYER_KEY = "PassN2";
    //KEY:b5e4efee-dbaa-4f12-986e-d009fb0eef5b

    public static void main(String[] args) throws IOException {
        Hero hero = new Hero(GAME_ID, PLAYER_NAME,PLAYER_KEY);


        Emitter.Listener onMapUpdate = new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                try {
                    GameMap gameMap = hero.getGameMap();
                    gameMap.updateOnUpdateMap(args[0]);

                    Player player = gameMap.getCurrentPlayer();

                    List<Player> otherPlayers = gameMap.getOtherPlayerInfo();
                    List<Obstacle> restrictedList = gameMap.getListIndestructibleObstacles();
                    restrictedList.addAll(gameMap.getListChests());
                    restrictedList.addAll(gameMap.getListTraps());

                    List<Node> restrictedPoints = new ArrayList<>();
                    for (Obstacle o : restrictedList) {
                        restrictedPoints.add(new Node(o.getX(), o.getY()));
                    }
//================================================================================================================

                    Weapon currentGun = hero.getInventory().getGun(); //hero's gun
                    Weapon currentMelee = hero.getInventory().getMelee(); // hero's melee
                    Weapon currentThrowable = hero.getInventory().getThrowable();
                    List<Armor> currentArmor = hero.getInventory().getListArmor(); //hero's armors
                    List<HealingItem> HealingItem = hero.getInventory().getListHealingItem(); //healing

                    final List<Weapon> throwableList = Action.sortElement(gameMap.getAllThrowable(),player);
                    final List<Weapon> meleeList = Action.sortElement(gameMap.getAllMelee(), player);
                    final List<Obstacle> chestList = Action.sortElement(gameMap.getListChests(), player);

                    final List<Bullet> bulletList = Action.sortElement(gameMap.getListBullets(),player);


                    Obstacle chest = chestList.getFirst();
                    restrictedPoints.addAll(restrictedList);

                    final List<Armor> armorList = Action.sortElement(gameMap.getListArmors(), player);
                    final List<HealingItem> healingItemList = Action.sortElement(gameMap.getListHealingItems(), player);
                    boolean fullArmor = HealingItem.size() == 4;
                    boolean fullHealing = armorList.size() == 2;

                    otherPlayers = Action.sortElement(otherPlayers, player);
                    Player target = otherPlayers.getFirst();

                    if (player.getHp() <= 80 && !HealingItem.isEmpty()) {

                        HealingItem selected = HealingItem.getFirst();
                        hero.useItem(selected.getId());

                        System.out.println("Using healing items1...");

                    }
                    if (Action.calDistance(player, target) <= 1.0 && target.getIsAlive()) {
                        String attackDirection = Action.actionAttack(player, target, currentMelee);
                        if (!Action.shot) {

                            hero.shoot(attackDirection);
                            Action.slashed = false;
                            Action.shot = true;
                            System.out.println("Shot: " + Action.shot + " Slashed: "+ Action.slashed);

                        }
                        if (!Action.slashed) {
                            hero.shoot(attackDirection);

                            Action.shot = false;
                            Action.slashed = true;
                        }
                    }





                    Node heal = new Node();
                    if(!PathUtils.checkInsideSafeArea(player, gameMap.getDarkAreaSize(), gameMap.getMapSize())) {
                        if (player.getX() < gameMap.getDarkAreaSize()) {
                            hero.move("r");
                        } else if (player.getX() > gameMap.getMapSize() - gameMap.getDarkAreaSize()) {
                            hero.move("l");
                        } else if (player.getY() < gameMap.getDarkAreaSize()) {
                            hero.move("u");
                        } else if (player.getY() > gameMap.getMapSize() - gameMap.getDarkAreaSize()) {
                            hero.move("d");
                        }
                    }
                    if(!healingItemList.isEmpty()) {
                        int healingIndex = 0;
                         while (!PathUtils.checkInsideSafeArea(healingItemList.get(healingIndex), gameMap.getDarkAreaSize(), gameMap.getMapSize())
                                || PathUtils.getShortestPath(gameMap, restrictedPoints, player, healingItemList.get(healingIndex), false)==null){
                            if (healingIndex == healingItemList.size()-1){
                                break;
                            }
                            healingIndex++;
                        }
                        if (PathUtils.getShortestPath(gameMap, restrictedPoints, player, healingItemList.get(healingIndex), false) != null)
                            heal = healingItemList.get(healingIndex);

                    }

                    Node armor = new Node();
                    if(!armorList.isEmpty() ) {

                        int armorIndex = 0;
                        while (!PathUtils.checkInsideSafeArea(armorList.get(armorIndex), gameMap.getDarkAreaSize(), gameMap.getMapSize())
                                || PathUtils.getShortestPath(gameMap, restrictedPoints, player, armorList.get(armorIndex), false) == null) {
                            if (armorIndex == armorList.size() - 1) {
                                break;
                            }
                            armorIndex++;
                        }
                        if (PathUtils.getShortestPath(gameMap, restrictedPoints, player, armorList.get(armorIndex), false) != null)
                            armor = armorList.get(armorIndex);
                    }

                     Node melee = new Node();
                    if(!meleeList.isEmpty()) {
                        int meleeIndex = 0;
                        while (!PathUtils.checkInsideSafeArea(meleeList.get(meleeIndex), gameMap.getDarkAreaSize(), gameMap.getMapSize())
                                    || PathUtils.getShortestPath(gameMap, restrictedPoints, player, meleeList.get(meleeIndex), false)==null){
                                if (meleeIndex == meleeList.size()-1){
                                    break;
                                }
                                meleeIndex++;
                            }

                        if (PathUtils.getShortestPath(gameMap, restrictedPoints, player, meleeList.get(meleeIndex), false) != null)
                            melee = meleeList.get(meleeIndex);

                    }
                    if (currentGun == null) {
                        final List<Weapon> gunList = Action.sortElement(gameMap.getAllGun(), player);
                        Weapon targetWeapon = gunList.getFirst();
                        if(currentThrowable != null){
                            hero.throwItem(Action.actionAttack(player,target,currentThrowable));
                        }else {
                            Action.navigateItem(hero, player, targetWeapon, restrictedList, restrictedPoints, gameMap);
                        }

                        }

                    else if(currentGun != null) {


                        restrictedPoints.addAll(restrictedList);

                        if (Action.calDistance(player, chest) <= 2.0 &&HealingItem.size()<4&& !fullArmor && Action.calDistance(player, target)> currentGun.getRange()) {
                            hero.shoot(Action.actionAttack(player, chest, currentGun));

                        }


                            if (heal != null && !fullHealing && Action.calDistance(player, heal) < 4) {
                                System.out.println("Finding healing items....");

                                //HealingItem heal = healingItemList.getFirst();
                               String movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, heal, false);
                                System.out.println(movement);
                                Action.navigateItem(hero, player, heal, restrictedList, restrictedPoints, gameMap);
                                healingItemList.remove(heal);
                            }





                        else if (!currentArmor.contains(armor) && currentArmor.size()<2 && !armorList.isEmpty() && !fullArmor && Action.calDistance(player,armor) < 4) {
                            System.out.println(currentArmor.getLast().equals(currentArmor.getFirst()));
                            if(currentArmor.isEmpty()||(!currentArmor.getFirst().getType().equals(((Armor)armor).getType()) &&currentArmor.getLast().equals(currentArmor.getFirst())|| !currentArmor.get(1).getType().equals(((Armor)armor).getType()))) {
                                System.out.println("Finding Armor....");

                                String movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, armor, false);
                                System.out.println(movement);

//                            if(Objects.equals(currentArmor.get(0).getId(), "HELMET") && Objects.equals(armor.getId(), "POT")){
//                                hero.revokeItem(currentArmor.get(0).getId());
//                            }else if(Objects.equals(currentArmor.get(1).getId(), "HELMET") && Objects.equals(armor.getId(), "POT")){
//                                hero.revokeItem(currentArmor.get(1).getId());
//                            }

                                Action.navigateItem(hero, player, armor, restrictedList, restrictedPoints, gameMap);
                                armorList.remove(armor);
                            }
                        }


                        else if(currentMelee.getId().equals("HAND") && Action.calDistance(player,melee) <= 3.0) {
                            String movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, melee,false);
                            hero.move(movement);
                            System.out.println(movement);
                            Action.navigateItem(hero,player,melee,restrictedList,restrictedPoints,gameMap);
                        }
                      else if(currentThrowable == null && !throwableList.isEmpty() && Action.calDistance(player,throwableList.getFirst()) <= 3.0) {

                                Weapon throwable = throwableList.getFirst();
                                String movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, throwable, false);
                                Action.navigateItem(hero,player,melee,restrictedList,restrictedPoints,gameMap);
                                System.out.println(movement);
                                hero.pickupItem();
                            }
                        else {
                                int i = 0;
                                while (!target.getIsAlive()) {
                                    i++;
                                    target = otherPlayers.get(i);
                                }


                                hero.shoot(Action.actionAttack(player, target, currentGun));
                                otherPlayers.remove(target);

                                String movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, target, false);
                            hero.move(movement);
                        }
                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        hero.setOnMapUpdate(onMapUpdate);
        hero.start(SERVER_URL);
    }
}