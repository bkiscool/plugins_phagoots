package net.pgfmc.core.bot.discord.listeners;

import java.util.stream.Collectors;

import org.bukkit.ChatColor;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.pgfmc.core.bot.discord.Discord;
import net.pgfmc.core.bot.util.MessageHandler;
import net.pgfmc.core.util.roles.PGFRole;
import net.pgfmc.core.util.roles.Roles;

public class OnMessageReceived extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e)
	{
		final User user = e.getAuthor();
		
		final MessageHandler handler = new MessageHandler(e.getMessage().getContentDisplay(), user);
		
		if (handler.getMessage().length() == 0) return;
		
		// return if message isn't in #server or a bot
		if (!e.getChannel().getId().equals(Discord.getServerChannel().getId()) || user.isBot()) return;
		
		PGFRole memberRole = PGFRole.MEMBER;
		
		// If member of PGF (mainly for BTS/outside PGF server)
		if (Discord.getGuildPGF().isMember(user))
		{
			memberRole = Roles.getTop(Discord.getMemberRoles(user.getId())
					.stream()
					.map(roleName -> PGFRole.get(roleName))
					.collect(Collectors.toList()).stream()
					.filter(role -> role != null)
					.collect(Collectors.toList()));
			
		}
		
		// If not reply
		if(e.getMessage().getReferencedMessage() == null || e.getMessage().getReferencedMessage().getAuthor().isBot() || Discord.getGuildPGF() == null)
		{
			handler.setMessage(memberRole.getColor()
					+ ((memberRole.compareTo(PGFRole.STAFF) <= 0) ? PGFRole.STAFF_DIAMOND : "")
					+ e.getMember().getEffectiveName()
					+ ChatColor.RESET + ChatColor.DARK_GRAY + " -|| "
					+ MessageHandler.getTrackColor(e.getMember().getId())
					+ handler.getMessage());
			
			handler.send();
			
			return;
		} else {
            PGFRole replyRole = PGFRole.MEMBER;
            Member replyMember = Discord.getGuildPGF().getMember(e.getMessage().getReferencedMessage().getAuthor());
            
            if (replyMember != null)
            {
            	replyRole = Roles.getTop(Discord.getMemberRoles(replyMember.getId())
						.stream()
						.map(roleName -> PGFRole.get(roleName))
						.collect(Collectors.toList()).stream()
						.filter(role -> role != null)
						.collect(Collectors.toList()));
            }
            
            handler.setMessage(memberRole.getColor()
            		+ ((memberRole.compareTo(PGFRole.STAFF) <= 0) ? PGFRole.STAFF_DIAMOND : "")
            		+ e.getMember().getEffectiveName()
            		+ " replied to "
            		+ replyRole.getColor()
            		+ ((memberRole.compareTo(PGFRole.STAFF) <= 0) ? PGFRole.STAFF_DIAMOND : "")
            		+ replyMember.getEffectiveName()
            		+ ChatColor.RESET + ChatColor.DARK_GRAY + " || "
            		+ MessageHandler.getTrackColor(e.getMember().getId())
            		+ handler.getMessage());
            
            handler.send();
            
            return;
            
		}
		
	}
	
}

