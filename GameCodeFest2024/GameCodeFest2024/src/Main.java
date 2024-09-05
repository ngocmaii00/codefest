import io.socket.emitter.Emitter;
import jsclub.codefest2024.sdk.Hero;
import jsclub.codefest2024.sdk.algorithm.PathUtils;
import jsclub.codefest2024.sdk.base.Node;
import jsclub.codefest2024.sdk.model.GameMap;
import jsclub.codefest2024.sdk.model.enemies.Enemy;
import jsclub.codefest2024.sdk.model.obstacles.Obstacle;
import jsclub.codefest2024.sdk.model.players.Player;
import jsclub.codefest2024.sdk.model.weapon.Weapon;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    private static final String SERVER_URL = "https://cf-server.jsclub.dev";
    private static final String GAME_ID = "126517";
    private static final String PLAYER_NAME = "lilip";

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
                    if(currentWeapon !=null) System.out.println("ID: " + currentWeapon.getId());

                    if (currentWeapon == null){
                        final List<Weapon> gunList = sortGunList(gameMap.getAllGun(), player);
                        Weapon targetWeapon = gunList.getFirst();
                        System.out.println("Target: "+targetWeapon.getId());
                        if(player.getX()==targetWeapon.getX() && player.getY()==targetWeapon.getY()){
                            hero.pickupItem();
                        }else{
                            restrictedPoints.addAll(otherPlayers);
                            hero.move(PathUtils.getShortestPath(gameMap, restrictedPoints, player, targetWeapon, false));
                        }
                    }else if(currentWeapon != null){
                        
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