package maplestory.server.net.handlers.channel;

import constants.MessageType;
import io.netty.buffer.ByteBuf;
import maplestory.client.MapleClient;
import maplestory.guild.MapleGuild;
import maplestory.guild.bbs.BulletinEmote;
import maplestory.guild.bbs.BulletinPost;
import maplestory.guild.bbs.GuildBulletin;
import maplestory.server.net.MaplePacketHandler;
import maplestory.server.net.PacketFactory;
import maplestory.util.Hex;

public class GuildBBSHandler extends MaplePacketHandler {

	@Override
	public void handle(ByteBuf buf, MapleClient client) throws Exception {
		byte operation = buf.readByte();
	
		MapleGuild guild = client.getCharacter().getGuild();
		
		if(guild == null){
			client.sendReallowActions();
			return;
		}
		
		GuildBulletin bulletin = guild.getBulletin();
		
		if(operation == 0){
			
			boolean edit = buf.readBoolean();
			int postId = -1;
			if(edit){
				postId = buf.readInt();
			}
			boolean notice = buf.readBoolean();
			String title = readMapleAsciiString(buf);
			String text = readMapleAsciiString(buf);
			
			int icon = buf.readInt();
			
			BulletinEmote emote = BulletinEmote.getById(icon);
			
			if(emote.isPremium()){
				if(client.getCharacter().getItemQuantity(5290000 + icon - 0x64, false) == 0){
					emote = BulletinEmote.SMILE;
				}
			}
			
			if(edit){
				bulletin.editPost(postId, title, text, icon, client.getCharacter());
			}else if(!notice){
				bulletin.addPost(title, text, emote, client.getCharacter());
			}else{
				bulletin.setNotice(title, text, emote, client.getCharacter());
			}
			
		}else if(operation == 1){
			
			int postId = buf.readInt();
			
			bulletin.deletePost(postId);
			
		}else if(operation == 2){//Open BBS
			@SuppressWarnings(value="unused")
			int page = buf.readInt();
			
			client.getCharacter().sendGuildBulletin(bulletin);
		}else if(operation == 3){
			
			int postId = buf.readInt();
			
			BulletinPost post = bulletin.getPost(postId);
			
			if(postId == 0){
				post = bulletin.getNotice();
			}
			
			if(post != null){
				client.sendPacket(PacketFactory.guildBBSThread(post));
			}
			
		}else{
			System.out.println("Unknown "+operation);
		}
		
	}

}
