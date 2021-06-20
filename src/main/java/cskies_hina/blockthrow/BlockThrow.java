package cskies_hina.blockthrow;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.block.Action;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.bukkit.entity.FallingBlock;

public class BlockThrow extends JavaPlugin implements Listener{
	
	final boolean ENABLE_CONSOLE_LOGGING = true;
	
	private double throw_mult;
	private boolean enable_drops;
	private boolean enable_damage;
	
    // This code is called after the server starts and after the /reload command
    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "{0}.onEnable()", this.getClass().getName());
        
        //this.getConfig().addDefault("throw_mult", 0.7);
        //this.getConfig().addDefault("enable_drops", false);
        //this.getConfig().addDefault("enable_damage", true);
        
        //this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        
        this.throw_mult = Double.valueOf(this.getConfig().getString("throw_mult"));
        //this.throw_mult = this.getConfig().getDouble("throw_mult");
        this.enable_drops = this.getConfig().getBoolean("enable_drops");
        this.enable_damage = this.getConfig().getBoolean("enable_damage");
        
        if(ENABLE_CONSOLE_LOGGING)
        {
        	String out = "throw mult: " + throw_mult;
        	out += ", drops?: " + enable_drops;
			out += ", damage?: " + enable_damage;

	        getLogger().log(Level.INFO, "{0}.onEnable()", out);
        }
        
        getServer().getPluginManager().registerEvents(this, this);
    }

    // This code is called before the server stops and after the /reload command
    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "{0}.onDisable()", this.getClass().getName());
    }
    
    @EventHandler
	private void onPlayerInteract(PlayerInteractEvent event)
	{
    	if(	!event.getPlayer().isSneaking()
    		&& (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)
    		&& event.getHand().equals(EquipmentSlot.HAND)
    		&& !event.hasItem())
    	{
	    		throwBehaviour(event.getPlayer(), event.getClickedBlock(), event.getAction());
    	}
	}
    
    private void throwBehaviour(Player player, Block block, Action action)
    {
		BlockData blockdata = block.getBlockData();
		Location location = block.getLocation().add(0.5, 0, 0.5);
		
		//Simulate Block Breaking
		if(	action == Action.RIGHT_CLICK_BLOCK || player.getGameMode() != GameMode.CREATIVE)
		{
			block.getWorld().playSound(location, blockdata.getSoundGroup().getBreakSound(), 1, 1);
			block.getWorld().playEffect(location, Effect.STEP_SOUND, blockdata.getMaterial());
    		block.setType(Material.AIR);
		}
		
		//Adjust velocity
		Vector direction = player.getLocation().getDirection();
		Vector velocity = direction.multiply(throw_mult);
		
    	if(	action == Action.LEFT_CLICK_BLOCK)
    	{
    		velocity.setY(Math.abs(velocity.getY()));
    	}
    	else if(action == Action.RIGHT_CLICK_BLOCK)
    	{
    		velocity.multiply(-1);
    	}
		
		FallingBlock fall = block.getWorld().spawnFallingBlock(location, blockdata);
		fall.setVelocity(velocity);

		fall.setDropItem(enable_drops);
		fall.setHurtEntities(enable_damage);
		
		if(ENABLE_CONSOLE_LOGGING)
		{
			String out = "Summoned " + blockdata.getMaterial().toString() + "{" + fall.getVelocity().toString() + "}";
			out += ", drops?: " + enable_drops;
			out += ", damage?: " + enable_damage;
			Bukkit.getLogger().log(Level.INFO, out);			
		}
    }
}
