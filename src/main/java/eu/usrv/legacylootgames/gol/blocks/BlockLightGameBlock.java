
package eu.usrv.legacylootgames.gol.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import eu.usrv.legacylootgames.LootGamesLegacy;
import eu.usrv.legacylootgames.gol.tiles.TELightGameBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.timeconqueror.lootgames.LootGames;


public class BlockLightGameBlock extends Block implements ITileEntityProvider {
    @SideOnly(Side.CLIENT)
    protected IIcon[] mTexturesOff;
    @SideOnly(Side.CLIENT)
    protected IIcon[] mTexturesOn;

    public BlockLightGameBlock() {
        super(Material.iron);
        setBlockUnbreakable();
        setUnlocalizedName("lightGame");
        setCreativeTab(LootGames.CREATIVE_TAB);
        setLightLevel(1.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return mTexturesOff[0];
    }

    @Override
    public final boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        mTexturesOff = new IIcon[9];
        mTexturesOn = new IIcon[9];

        for (int i = 0; i < 9; i++) {
            mTexturesOff[i] = register.registerIcon(String.format("LootGames:gameoflight/inactive/%d", i));
            mTexturesOn[i] = register.registerIcon(String.format("LootGames:gameoflight/active/%d", i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side) {
        TileEntity tile = iBlockAccess.getTileEntity(x, y, z);
        if (tile instanceof TELightGameBlock) {
            TELightGameBlock tOurTE = (TELightGameBlock) tile;
            int iconIDX = 0;

            // GameOfLights.mLog.info( String.format( "TE at %d %d %d master? %s ordinal? %s", tile.xCoord, tile.yCoord,
            // tile.zCoord, tOurTE.mIsMaster, tOurTE.mTEDirection.toString() ) );

            switch (tOurTE.getDirection()) {
                case UP: // Masterblock
                    iconIDX = 0;
                    break;
                case NORTH:
                    iconIDX = 2;
                    break;
                case SOUTH:
                    iconIDX = 7;
                    break;
                case WEST:
                    iconIDX = 4;
                    break;
                case EAST:
                    iconIDX = 5;
                    break;
                case NORTHEAST:
                    iconIDX = 3;
                    break;
                case NORTHWEST:
                    iconIDX = 1;
                    break;
                case SOUTHEAST:
                    iconIDX = 8;
                    break;
                case SOUTHWEST:
                    iconIDX = 6;
                    break;
                default:
                    iconIDX = 0;
                    break;
            }

            if (tOurTE.getIsActive())
                return mTexturesOn[iconIDX];
            else
                return mTexturesOff[iconIDX];
        }

        return null;
    }

    @Override
    public boolean onBlockActivated(World pWorld, int pX, int pY, int pZ, EntityPlayer pPlayer, int pSide, float pSubX, float pSubY, float pSubZ) {
        if (pWorld.isRemote)
            return true;
        else {
            if (!pWorld.isRemote && pWorld.getTileEntity(pX, pY, pZ) instanceof TELightGameBlock) {
                TELightGameBlock tileEntity = (TELightGameBlock) pWorld.getTileEntity(pX, pY, pZ);

                ItemStack inhandStack = null;
                if (LootGamesLegacy.DONOR_CONTROLLER != null && LootGamesLegacy.DONOR_CONTROLLER.isDonor(pPlayer))
                    inhandStack = pPlayer.getCurrentEquippedItem();

                if (inhandStack != null && inhandStack.getItem() == Items.diamond)
                    tileEntity.toggleMusicMode(pPlayer);
                else
                    tileEntity.onBlockClickedByPlayer(null, pPlayer);
            }
            return true;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World pWorld, int p_149915_2_) {
        return new TELightGameBlock();
    }
}
