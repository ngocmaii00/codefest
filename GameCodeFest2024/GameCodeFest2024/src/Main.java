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

public class Main {
    private static final String SERVER_URL = "https://cf-server.jsclub.dev";
    private static final String GAME_ID = "170043";
    private static final String PLAYER_NAME = "IDontKnowCode";

    public static double calDistance(Node x, Node y){
        return Math.sqrt(Math.pow(x.getX()-y.getX(),2) + Math.pow(x.getY()-y.getY(),2));
    }
    public static List<Weapon> sortGunList(List<Weapon> gunList, Player player){
        gunList.sort(new Comparator<Weapon>() {
            @Override
            public int compare(Weapon o1, Weapon o2) {
                if(calDistance(player, o1) < calDistance(player, o2)){
                    return -1;
                }else if(calDistance(player, o1) >= calDistance(player, o2)){
                    return 1;
                }
                return 0;
            }
        });
        return gunList;
    }

    public static List<Player> sortEnemy(List<Player> enemyList, Player player){
        enemyList.sort(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if(calDistance(player, o1) < calDistance(player, o2)){
                    return -1;
                }else if(calDistance(player, o1) >= calDistance(player, o2)){
                    return 1;
                }
                return 0;
            }
        });
        return enemyList;
    }

    public static List<Obstacle> sortChest(List<Obstacle> chestList, Player player){
        chestList.sort(new Comparator<Obstacle>() {
            @Override
            public int compare(Obstacle o1, Obstacle o2) {
                if(calDistance(player, o1) < calDistance(player, o2)){
                    return -1;
                }else if(calDistance(player, o1) >= calDistance(player, o2)){
                    return 1;
                }
                return 0;
            }
        });
        return chestList;
    }

    public static void main(String[] args) throws IOException {
        Hero hero = new Hero(GAME_ID, PLAYER_NAME);

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
                    for(Obstacle o : restrictedList){
                        restrictedPoints.add(new Node(o.getX(), o.getY()));
                    }

                    Weapon currentWeapon = hero.getInventory().getGun();

                    if (currentWeapon!=null){
                        System.out.println("ID: " + currentWeapon.getId());
                        final List<Player> enemies = sortEnemy(gameMap.getOtherPlayerInfo(), player);
                        int i = 0;
                        Player currentEnemy = enemies.get(i);
                        while(!enemies.get(i).getIsAlive()){
                            i++;
                            currentEnemy = enemies.get(i);
                        }

                        final List<Obstacle> chestList = sortChest(gameMap.getListChests(), player);
                        Node targetChest = chestList.getFirst();


                        System.out.println("Current Enemy: "+ currentEnemy.getPlayerName());
                        Node target = new Node(currentEnemy.getX(), currentEnemy.getY());
                        int dx = player.getX() - target.getX();
                        int dy = player.getY() - target.getY();

                        if((target.getX() == player.getX() && calDistance(target, player) <= currentWeapon.getRange())
                        || (target.getY() == player.getY() && calDistance(target, player) <= currentWeapon.getRange())){
                            if(dx > 0){
                                hero.shoot("l");
                            }else if (dx < 0){
                                hero.shoot("r");
                            }else if(dy > 0){
                                hero.shoot("d");
                            }else if (dy < 0){
                                hero.shoot("u");
                            }
                        } else if ((targetChest.getX()==player.getX() && calDistance(targetChest, player) <= currentWeapon.getRange())
                                || (targetChest.getY()==player.getY() && calDistance(targetChest, player) <= currentWeapon.getRange())){
                            if(dx > 0){
                                hero.shoot("l");
                            }else if (dx < 0){
                                hero.shoot("r");
                            }else if(dy > 0){
                                hero.shoot("d");
                            }else if (dy < 0){
                                hero.shoot("u");
                            }
                        } else{
                            String path = PathUtils.getShortestPath(gameMap, restrictedPoints, player, target, false);
                            hero.move(path);
                            System.out.println("Path: "+path);
                        }
                    }
                    else {
                        final List<Weapon> gunList = sortGunList(gameMap.getAllGun(), player);
                        Weapon targetWeapon = gunList.getFirst();
                        System.out.println("Target: "+targetWeapon.getId());
                        if(player.getX()==targetWeapon.getX() && player.getY()==targetWeapon.getY()){
                            hero.pickupItem();
                        }else{
                            restrictedPoints.addAll(otherPlayers);
                            String path = PathUtils.getShortestPath(gameMap, restrictedPoints, player, targetWeapon, false);
                            hero.move(path);
                            System.out.println("Path: "+path);
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