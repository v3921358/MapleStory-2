package maplestory.life.movement;

import tools.data.output.MaplePacketWriter;
import io.netty.buffer.ByteBuf;
import lombok.ToString;
import maplestory.map.AbstractAnimatedMapleMapObject;

@ToString
public class RelativeLifeMovement implements LifeMovement {
	
	private byte type;
	private int x, y;
	private byte state;
	private short duration;
	
	public RelativeLifeMovement(byte type, ByteBuf buf) {
		this.type = type;
		x = buf.readShort();
		y = buf.readShort();
		state = buf.readByte();
		duration = buf.readShort();
	}
	
	@Override
	public void encode(MaplePacketWriter buf) {
		buf.write(type);
		buf.writeShort(x);
		buf.writeShort(y);
		buf.write(state);
		buf.writeShort(duration);
	}

	@Override
	public void translateLife(AbstractAnimatedMapleMapObject life) {
		life.getPosition().translate(x, y);
		life.setStance(state);
	}
	
	@Override
	public MoveType getType() {
		return MoveType.RELATIVE;
	}

}
