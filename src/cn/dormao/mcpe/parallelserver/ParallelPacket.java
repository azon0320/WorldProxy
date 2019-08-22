package cn.dormao.mcpe.parallelserver;

public interface ParallelPacket {

    byte PK_VERSION = 23;

    //General
    byte PK_CLOSE = 0;
    byte PK_BATCHED = 1;
    byte PK_ERROR = 2;
    byte PK_WORLD_SET = 3;
    byte PK_TEST = 4;

    //WorldEvent
    byte PK_CHUNK_REQUEST = 5;
    byte PK_GET_SPAWN = 6;
    byte PK_SET_SPAWN = 7;
    byte PK_CHUNK_RESPONSE_BLOCKS = 8;
    byte PK_CHUNK_RESPONSE_METAS = 9;
    byte PK_CHUNK_RESPONSE_BIOMES = 10;
    byte PK_CHUNK_RESPONSE_FASTBIN_BLOCKS = 11;
    byte PK_CHUNK_RESPONSE_FASTBIN_METAS = 12;
    byte PK_WORLD_SET_BLOCK = 15;
    byte PK_WORLD_TIME = 16;

    //EntityEvent TODO AllPackets
    byte PK_ENTITY_SPAWN = 16;
    byte PK_ENTITY_DESPAWN = 17;
    byte PK_PLAYER_SPAWN = 18;
    byte PK_PLAYER_DESPAWN = 19;
    byte PK_ENTITY_ID = 20;
    byte PK_ENTITY_MOVE = 21;
    byte PK_ENTITY_HIT = 22;

    byte getPacketId();

    byte[] getEncoded();

    boolean requireSendIndependent();

    /**
     * @param raw byte array with no id
     */
    void doDecode(byte[] raw);
}
