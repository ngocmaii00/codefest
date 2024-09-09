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
    private static final String GAME_ID = "160974";
    private static final String PLAYER_NAME = "Retard";


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
//================================================================================================================

                    Weapon currentGun = hero.getInventory().getGun();
                    Weapon currentMelee = hero.getInventory().getMelee();
                    Weapon currentThrowAble = hero.getInventory().getThrowable();
                    List<Weapon> meleeList = gameMap.getAllMelee();
                    List<Weapon> throwableList = gameMap.getAllThrowable();

                    if(currentThrowAble !=null) System.out.println("ID: " + currentThrowAble.getId());

                    if (currentGun == null){
                        final List<Weapon> gunList = Action.sortElement(gameMap.getAllGun(), player);
                        Weapon targetWeapon = gunList.getFirst();

                        if(player.getX()==targetWeapon.getX() && player.getY()==targetWeapon.getY()){
                            hero.pickupItem();
                        }else{

                            restrictedPoints.addAll(restrictedList);
                            hero.move(PathUtils.getShortestPath(gameMap, restrictedPoints, player, targetWeapon, false));

                            ;
                        }
                    }else if(currentGun != null || currentMelee != null || currentThrowAble != null){
                        final List<Player>others = Action.sortElement(otherPlayers, player);
                        if(others.getFirst().getIsAlive() == false || others.getFirst().getHp() == 0)
                            others.remove(others.getFirst());

                         Player targetPlayer = others.getFirst();
                        Node target = new Node(targetPlayer.x,targetPlayer.y);

                        hero.shoot(Action.actionAttack(player, targetPlayer, currentGun));


                            final List<Obstacle> chestList = Action.sortElement(gameMap.getListChests(), player);
                            Obstacle chest = chestList.getFirst();
                            restrictedPoints.addAll(restrictedList);
                            String movement;
                            if(Action.calDistance(player,chest) <= 3.0 ) {
                                hero.shoot(Action.actionAttack(player, chest, currentGun));


                            }
                            if(currentMelee.getId() == "HAND" && !meleeList.isEmpty() && Action.calDistance(player,meleeList.getFirst()) <= 3.0){

                            Weapon melee = meleeList.getFirst();
                            movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, melee, false);
                            hero.move(movement);
                            System.out.println(movement);
                            hero.pickupItem();
                            }/*else if(currentThrowAble == null && !throwableList.isEmpty() && calDistance(player,throwableList.getFirst()) <= 3.0) {

                                Weapon throwable = throwableList.getFirst();
                                movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, throwable, false);
                                hero.move(movement);
                                System.out.println(movement);
                                hero.pickupItem();
                            }*/else {


                                movement = PathUtils.getShortestPath(gameMap, restrictedPoints, player, target, false);

                                }
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