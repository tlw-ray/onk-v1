package com.xskr.onk_v1.controller;

import com.xskr.onk_v1.core.Card;
import com.xskr.onk_v1.core.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/onk_v1")
public class ONKController{

    private static int RoomID_Generator = 0;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Integer, Room> idRoomMap = Collections.synchronizedMap(new TreeMap());

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(path = "/hello")
    public String hello(){
        return "Hello";
    }

    /**
     * 创建房间，需要传入该房间支持的角色列表
     * @param cardNames 角色名称清单
     * @return 房间静态信息, ID, 现有玩家清单, 座位数量等, 角色列表
     */
    @RequestMapping(path = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Room createRoom(@RequestBody String[] cardNames){
        logger.debug(Arrays.toString(cardNames));
        List<Card> cards = new ArrayList();
        for(String cardName:cardNames){
            Card card = Card.valueOf(cardName);
            cards.add(card);
        }
        RoomID_Generator++;
        Room room = new Room(RoomID_Generator, cards);
        room.setSimpMessagingTemplate(simpMessagingTemplate);
        idRoomMap.put(RoomID_Generator, room);
        return room;
    }

    /**
     * 玩家进入房间, 返回当前房间的座位到玩家名称的映射关系
     * @param roomID
     * @return
     */
    @RequestMapping("/{roomID}/{userName}/join")
    public Room join(@PathVariable int roomID, @PathVariable String userName){
        Room room = idRoomMap.get(roomID);
        room.join(userName);
        return room;
    }

    @RequestMapping(path = "/{roomID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Room getRoom(@PathVariable int roomID){
        Room room = idRoomMap.get(roomID);
        return room;
    }

    /**
     * 玩家离开
     */
    @RequestMapping("/{roomID}/{userName}/leave")
    public void leave(@PathVariable int roomID, @PathVariable String userName){
        Room room = idRoomMap.get(roomID);
        room.leave(userName);
    }

    /**
     * 玩家选择座位
     * @param roomID
     * @param seat
     */
    @RequestMapping("/{roomID}/{userName}/sit/{seat}")
    public void sit(@PathVariable int roomID, @PathVariable String userName, @PathVariable int seat){
        Room room = idRoomMap.get(roomID);
        room.sit(userName, seat);
    }

    /**
     * 玩家设定准备, 如果所有玩家均已准备则触发游戏开始
     * @param roomID 房间ID
     * @param ready 是否准备
     * @return 服务端返回的该玩家准备状态
     */
    @RequestMapping("/{roomID}/{userName}/ready/{ready}")
    public boolean ready(@PathVariable int roomID, @PathVariable String userName, @PathVariable boolean ready){
        Room room = idRoomMap.get(roomID);
        return room.setReady(userName, ready);
    }

    /**
     * 捣蛋鬼换牌
     * @param roomID
     * @param seat1
     * @param seat2
     */
    @RequestMapping("/{roomID}/{userName}/troublemaker/exchange/{seat1}/{seat2}")
    public void exchangeCard(@PathVariable int roomID, @PathVariable String userName, @PathVariable int seat1, @PathVariable int seat2){
        System.out.println("troublemaker exchange card: " + seat1 + ", " + seat2);
        Room room = idRoomMap.get(roomID);
        room.troublemakerExchangeCard(userName, seat1, seat2);
    }

    /**
     * 强盗换牌
     * @param roomID
     * @param seat
     */
    @RequestMapping("/{roomID}/{userName}/robber/snatch/{seat}")
    public void snatchCard(@PathVariable int roomID, @PathVariable String userName, @PathVariable int seat){
        Room room = idRoomMap.get(roomID);
        room.robberSnatchCard(userName, seat);
    }

    /**
     * 狼人验牌
     * @param roomID
     * @param deck
     */
    @RequestMapping("/{roomID}/{userName}/singleWolf/check/{deck}")
    public void wolfCheckDeck(@PathVariable int roomID, @PathVariable String userName, @PathVariable int deck){
        Room room = idRoomMap.get(roomID);
        room.singleWolfCheckDeck(userName, deck);
    }

    /**
     * 预言家验牌
     * @param roomID
     * @param deck1
     * @param deck2
     */
    @RequestMapping("/{roomID}/{userName}/seer/check/{deck1}/{deck2}")
    public void seerCheckDeck(@PathVariable int roomID, @PathVariable String userName, @PathVariable int deck1, @PathVariable int deck2){
        Room room = idRoomMap.get(roomID);
        room.seerCheckDeck(userName, deck1, deck2);
    }

    /**
     * 预言家验人
     * @param roomID
     * @param seat
     */
    @RequestMapping("/{roomID}/seer/check/{seat}")
    public void seerCheckPlayer(@PathVariable int roomID, @PathVariable String userName, @PathVariable int seat){
        Room room = idRoomMap.get(roomID);
        room.seerCheckPlayer(userName, seat);
    }

    /**
     * 酒鬼换牌
     * @param roomID
     * @param deck
     */
    @RequestMapping("/{roomID}/{userName}/drunk/exchange/{deck}")
    public void drunkExchangeCard(@PathVariable int roomID, @PathVariable String userName, @PathVariable int deck){
        Room room = idRoomMap.get(roomID);
        room.drunkExchangeCard(userName, deck);
    }

    /**
     * 投票
     * @param roomID
     * @param seat
     */
    @RequestMapping("/{roomID}/{userName}/vote/{seat}")
    public void vote(@PathVariable int roomID, @PathVariable String userName, @PathVariable int seat){
        Room room = idRoomMap.get(roomID);
        room.vote(userName, seat);
    }
}
