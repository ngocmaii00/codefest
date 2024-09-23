// import io.socket.emitter.Emitter;
//import jsclub.codefest2024.sdk.Hero;
//import jsclub.codefest2024.sdk.algorithm.PathUtils;
//import jsclub.codefest2024.sdk.base.Node;
//import jsclub.codefest2024.sdk.model.GameMap;
//import jsclub.codefest2024.sdk.model.equipments.Armor;
//import jsclub.codefest2024.sdk.model.equipments.HealingItem;
//import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
//import jsclub.codefest2024.sdk.model.players.Player;
//import jsclub.codefest2024.sdk.model.weapon.Weapon;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
// public class based {
//        private static final String SERVER_URL = "https://cf-server.jsclub.dev";
//        private static final String GAME_ID = "196390";
//        private static final String PLAYER_NAME = "SunnySD";
//        private static final String PLAYER_KEY = "b5e4efee-dbaa-4f12-986e-d009fb0eef5b";
//
//
//        public static void based(String[] args) throws IOException {
//            Hero hero = new Hero(GAME_ID, PLAYER_NAME, PLAYER_KEY);
//            SortList sortList = new SortList();
//
//            Emitter.Listener onMapUpdate = new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//
//                    try {
//                        GameMap gameMap = hero.getGameMap();
//                        gameMap.updateOnUpdateMap(args[0]);
//
//                        List<Node> emptyList = new ArrayList<>();
//
//                        Player player = gameMap.getCurrentPlayer();
//
//                        List<Player> otherPlayers = gameMap.getOtherPlayerInfo();
//
//                        List<Obstacle> restrictedList = gameMap.getListIndestructibleObstacles();
//                        restrictedList.addAll(gameMap.getListChests());
//                        restrictedList.addAll(gameMap.getListTraps());
//
//                        List<Node> restrictedPoints = new ArrayList<>();
//                        for(Obstacle o : restrictedList){
//                            restrictedPoints.add(new Node(o.getX(), o.getY()));
//                        }
//
//                        Weapon currentWeapon = hero.getInventory().getGun();
//                        Weapon currentMelee = hero.getInventory().getMelee();
//                        List<HealingItem> currentHealingItems = hero.getInventory().getListHealingItem(); //size = 4
//                        List<Armor> currentArmorList = hero.getInventory().getListArmor(); //1 top, 1 head
//
//                        //find melee
//                        List<Weapon> meleeList = SortList.sortGunList(gameMap.getAllMelee(), player);
//                        Node targetMelee = new Node();
//                        if (!meleeList.isEmpty()) {
//                            int meleeIndex = 0;
//                            while (!PathUtils.checkInsideSafeArea(meleeList.get(meleeIndex), gameMap.getDarkAreaSize(), gameMap.getMapSize())
//                                    || PathUtils.getShortestPath(gameMap, restrictedPoints, player, meleeList.get(meleeIndex), false)==null){
//                                if (meleeIndex == meleeList.size()-1){
//                                    break;
//                                }
//                                meleeIndex++;
//                            }
//                            if (PathUtils.getShortestPath(gameMap, restrictedPoints, player, meleeList.get(meleeIndex), false)!=null) {
//                                targetMelee = meleeList.get(meleeIndex);
//                            }
//                        }
//
//
//                        //closest enemy
//                        final List<Player> enemies = SortList.sortEnemy(gameMap.getOtherPlayerInfo(), player);
//                        int i = 0;
//                        Player currentEnemy = enemies.get(i);
//                        while ((!currentEnemy.getIsAlive() || !PathUtils.checkInsideSafeArea(currentEnemy, gameMap.getDarkAreaSize(), gameMap.getMapSize())
//                                || PathUtils.getShortestPath(gameMap, restrictedPoints, player, currentEnemy, false) == null)) {
//                            if (i==enemies.size()-1){
//                                break;
//                            }
//                            i++;
//                            currentEnemy = enemies.get(i);
//                        }
//
//                        Node target = new Node(currentEnemy.getX(), currentEnemy.getY());
//                        int dx = player.getX() - target.getX();
//                        int dy = player.getY() - target.getY();
//
//                        //find healing item
//                        List<HealingItem> healingItemList = SortList.sortHealingItem(gameMap.getListHealingItems(), player);
//                        Node targetHealing = new Node();
//                        if (!healingItemList.isEmpty()) {
//                            int healingIndex = 0;
//                            while (!PathUtils.checkInsideSafeArea(healingItemList.get(healingIndex), gameMap.getDarkAreaSize(), gameMap.getMapSize())
//                                    || PathUtils.getShortestPath(gameMap, restrictedPoints, player, healingItemList.get(healingIndex), false)==null){
//                                if (healingIndex == healingItemList.size()-1){
//                                    break;
//                                }
//                                healingIndex++;
//                            }
//                            if (PathUtils.getShortestPath(gameMap, restrictedPoints, player, healingItemList.get(healingIndex), false)!=null) {
//                                targetHealing = healingItemList.get(healingIndex);
//                            }
//                        }
//
//                        //find armorItem
//                        List<Armor> armorList = SortList.sortArmor(gameMap.getListArmors(), player);
//                        Node targetArmor = new Node();
//                        if (!armorList.isEmpty()) {
//                            int armorIndex = 0;
//                            while (!PathUtils.checkInsideSafeArea(armorList.get(armorIndex), gameMap.getDarkAreaSize(), gameMap.getMapSize()) ||
//                                    PathUtils.getShortestPath(gameMap, restrictedPoints, player, armorList.get(armorIndex), false)==null){
//                                if (armorIndex == armorList.size()-1){
//                                    break;
//                                }
//                                armorIndex++;
//                            }
//                            if (PathUtils.getShortestPath(gameMap, restrictedPoints, player, armorList.get(armorIndex), false)!=null) {
//                                targetArmor = armorList.get(armorIndex);
//                            }
//                        }
//
//                        //attack player
//                        boolean checkMelee = false;
//                        if (currentMelee.getId().compareTo("HAND")!=0) {
//                            checkMelee = (SortList.calDistance(player, target) == 1);
//                        }
//
//                        if (checkMelee){
//                            if (dy == 0) {
//                                if (dx > 0) {
//                                    hero.attack("l");
//                                } else if (dx < 0) {
//                                    hero.attack("r");
//                                }
//                            } else {
//                                if (dy > 0) {
//                                    hero.attack("d");
//                                } else {
//                                    hero.attack("u");
//                                }
//                            }
//
//                        }
//                        else if (currentWeapon!=null) {
//
//                            //closest chest
//                            List<Obstacle> chestList = SortList.sortChest(gameMap.getListChests(), player);
//                            while (!PathUtils.checkInsideSafeArea(chestList.getFirst(), gameMap.getDarkAreaSize(), gameMap.getMapSize())){
//                                chestList.removeFirst();
//                            }
//                            Node targetChest = chestList.getFirst();
//                            int cx = player.getX() - targetChest.getX();
//                            int cy = player.getY() - targetChest.getY();
//
//                            //shoot player
//                            if ((dx == 0 && Math.abs(dy) <= currentWeapon.getRange()) || (dy == 0 && Math.abs(dx) <= currentWeapon.getRange())) {
//                                if (dy == 0) {
//                                    if (dx > 0) {
//                                        hero.shoot("l");
//                                    } else if (dx < 0) {
//                                        hero.shoot("r");
//                                    }
//                                } else {
//                                    if (dy > 0) {
//                                        hero.shoot("d");
//                                    } else {
//                                        hero.shoot("u");
//                                    }
//                                }
//                            } else if (targetArmor!=null && currentArmorList.size() < 2 && SortList.calDistance(targetArmor, player) <= 6){
//
//                                if (player.getX()!=targetArmor.getX() || player.getY()!=targetArmor.getY()){
//                                    String path = PathUtils.getShortestPath(gameMap, emptyList, player, targetArmor, false);
//                                    hero.move(path);
//                                }
//                            } else if (targetHealing!=null && currentHealingItems.size() < 4 && SortList.calDistance(targetHealing, player) <= 6){
//
//                                if (player.getX()!=targetHealing.getX() || player.getY()!=targetHealing.getY()){
//                                    String path = PathUtils.getShortestPath(gameMap, emptyList, player, targetHealing, false);
//                                    hero.move(path);
//                                }
//                            }else if (currentMelee.getId().compareTo("HAND")==0 && SortList.calDistance(targetMelee, player)<4){
//                                if (player.getX()==targetMelee.getX() && player.getY()==targetMelee.getY()){
//                                    hero.pickupItem();
//                                }else{
//                                    String path = PathUtils.getShortestPath(gameMap, restrictedPoints, player, targetMelee, false);
//                                    hero.move(path);
//                                }
//                            } else if ((currentHealingItems.size()<4 || currentArmorList.size() < 2) && ((cx == 0 && Math.abs(cy) <= 4) || (cy == 0 && Math.abs(cx) <= 4))){
//                                System.out.println("chest "+ targetChest.getX() + " "+targetChest.getY());
//                                if (cx == 0 && cy < 0) {
//                                    hero.shoot("u");
//                                } else if (cx == 0 && cy > 0) {
//                                    hero.shoot("d");
//                                } else if (cx > 0) {
//                                    hero.shoot("l");
//                                } else if (cx < 0) {
//                                    hero.shoot("r");
//                                }
//                            } else{
//                                String path = PathUtils.getShortestPath(gameMap, restrictedPoints, player, target, false);
//                                hero.move(path);
//                            }
//                        }else if (SortList.calDistance(player, target) <= 4) {
//                            if (SortList.calDistance(player, target) ==1 ) {
//                                if (dy == 0) {
//                                    if (dx > 0) {
//                                        hero.attack("l");
//                                    } else if (dx < 0) {
//                                        hero.attack("r");
//                                    }
//                                } else {
//                                    if (dy > 0) {
//                                        hero.attack("d");
//                                    } else {
//                                        hero.attack("u");
//                                    }
//                                }
//                            }else {
//                                String path = PathUtils.getShortestPath(gameMap, restrictedPoints, player, target, false);
//                                hero.move(path);
//                            }
//                        }
////                    find gun
//                        else {
//                            final List<Weapon> gunList = SortList.sortGunList(gameMap.getAllGun(), player);
//                            int m = 0;
//                            Weapon targetWeapon = gunList.get(m);
//                            while (m<gunList.size()-1 && !PathUtils.checkInsideSafeArea(targetWeapon, gameMap.getDarkAreaSize(), gameMap.getMapSize())) {
//                                m++;
//                                targetWeapon = gunList.get(m);
//                            }
//                            if (player.getX() == targetWeapon.getX() && player.getY() == targetWeapon.getY()) {
//                                hero.pickupItem();
//                            } else {
//                                restrictedPoints.addAll(otherPlayers);
//                                String path = PathUtils.getShortestPath(gameMap, restrictedPoints, player, targetWeapon, false);
//                                hero.move(path);
//                            }
//
//                        }
//
//                        //use healing items
//                        if (!currentHealingItems.isEmpty() && SortList.calDistance(player, currentEnemy) > 8 && player.getHp()<=90){
//                            hero.useItem(currentHealingItems.getFirst().getId());
//                        }
//
//                        if (currentArmorList.size() < 2 && targetArmor!=null && player.getX()==targetArmor.getX() && player.getY()==targetArmor.getY()){
//                            if ((gameMap.getElementByIndex(targetArmor.getX(), targetArmor.getY()).getId().compareTo("POT")==0
//                                    || gameMap.getElementByIndex(targetArmor.getX(), targetArmor.getY()).getId().compareTo("HELMET")==0)
//                                    && sortList.getCountHead()<=1){
//                                hero.pickupItem();
//                                sortList.setCountHead();
//                            }else {
//                                hero.pickupItem();
//                            }
//                        }
//
//                        if (currentHealingItems.size() < 4 && targetHealing!=null && player.getX()==targetHealing.getX() && player.getY()==targetHealing.getY()){
//                            hero.pickupItem();
//                        }
//
//
//
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            };
//
//            hero.setOnMapUpdate(onMapUpdate);
//            hero.start(SERVER_URL);
//        }
//
//}
