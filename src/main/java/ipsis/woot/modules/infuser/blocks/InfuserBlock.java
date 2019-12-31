package ipsis.woot.modules.infuser.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class InfuserBlock extends Block {

    public InfuserBlock() {
        super(Properties.create(Material.IRON).sound(SoundType.METAL));
        setDefaultState(getStateContainer().getBaseState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InfuserTileEntity();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote)
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        TileEntity te = worldIn.getTileEntity(pos);
        ItemStack heldItem = player.getHeldItem(handIn);
        if (!heldItem.isEmpty() && te instanceof InfuserTileEntity) {
            return ((InfuserTileEntity) te).handleFluidContainerUse(heldItem, player, handIn);
        } else {
            if (te instanceof INamedContainerProvider)
                NetworkHooks.openGui(
                        (ServerPlayerEntity) player,
                        (INamedContainerProvider) te,
                        te.getPos());
            else
                throw new IllegalStateException("Named container provider is missing");
        }
        return true;
    }
}