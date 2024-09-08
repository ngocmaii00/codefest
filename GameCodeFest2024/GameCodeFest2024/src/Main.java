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
    private static final String GAME_ID = "112792";
    private static final String PLAYER_NAME = "Retard";

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

    public static List<Player> sortPlayer(List<Player> players, Player player){
        Comparator <Player> compare = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if(calDistance(player,o1) < calDistance(player,o2))
                    return -1;
                else if (calDistance(player,o1) >= calDistance(player,o2))
                    return 1;
                return 0;
            }
        };
        players.sort(compare);
        return players;
    }
    public static List<Obstacle> sortChest(List<Obstacle> chest, Node player){
        Comparator <Obstacle> compare = new Comparator<Obstacle>() {
            @Override
            public int compare(Obstacle o1, Obstacle o2) {
                if(calDistance(player,o1) < calDistance(player,o2))
                    return -1;
                else if (calDistance(player,o1) >= calDistance(player,o2))
                    return 1;
                return 0;
            }
        };
        chest.sort(compare);
        return chest;
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

                    Weapon currentGun = hero.getInventory().getGun();
                    Weapon currentMelee = hero.getInventory().getMelee();

                    Weapon currentThrowAble = hero.getInventory().getThrowable();
                    List<Weapon> meleeList = gameMap.getAllMelee();
                    List<Weapon> throwableList = gameMap.getAllThrowable();
                    if(currentThrowAble !=null) System.out.println("ID: " + currentThrowAble.getId());

                    if (currentGun == null){
                        final List<Weapon> gunList = sortGunList(gameMap.getAllGun(), player);
                        Weapon targetWeapon = gunList.getFirst();

                        if(player.getX()==targetWeapon.getX() && player.getY()==targetWeapon.getY()){
                            hero.pickupItem();
                        }else{

                            restrictedPoints.addAll(restrictedList);
                            hero.move(PathUtils.getShortestPath(gameMap, restrictedPoints, player, targetWeapon, false));

                            ;
                        }
                    }else if(currentGun != null){
                        List<Player>others = sortPlayer(otherPlayers, player);
                        if(others.getFirst().getIsAlive() == false || others.getFirst().getHp() == 0)
                            others.remove(others.getFirst());

                        final Player targetPlayer = others.getFirst();
                        Node target = new Node(targetPlayer.x,targetPlayer.y);

                        if(player.getX()== targetPlayer.x){
                            System.out.print(player.getX());
                            if(player.getY() <= targetPlayer.y + currentGun.getRange() && player.getY() > targetPlayer.y )
                                hero.shoot("d");

                            else if(player.getY() >= targetPlayer.y - currentGun.getRange() && player.getY() < targetPlayer.y)
                                hero.shoot("u");

                        }else if(player.getY() == targetPlayer.y ){
                            System.out.print(currentMelee.getId());
                            if(player.getX()  <= targetPlayer.x + currentGun.getRange() && player.getX() > targetPlayer.x)
                                hero.shoot("l");

                            else if(player.getX() >= targetPlayer.x - currentGun.getRange() && player.getX() < targetPlayer.x )
                                hero.shoot("r");

                        }
                            final List<Obstacle> chestList = sortChest(gameMap.getListChests(), player);
                            Obstacle chest = chestList.getFirst();
                            restrictedPoints.addAll(restrictedList);
                            String movement;
                            if(calDistance(player,chest) <= 3.0 ) {
                                hero.shoot(Action.actionAttack(player, chest, currentGun));


                            }
                            if(currentMelee.getId() == "HAND" && !meleeList.isEmpty() && calDistance(player,meleeList.getFirst()) <= 3.0){

                            Weapon melee = meleeList.getFirst();
                            movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, melee, false);
                            System.out.println(movement);
                            }else if(currentThrowAble == null && !throwableList.isEmpty() && calDistance(player,throwableList.getFirst()) <= 3.0) {

                            Weapon throwable = throwableList.getFirst();
                            movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, throwable, false);
                            System.out.println(movement);
                            }


                                movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, target, false);


                                hero.move(movement);


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