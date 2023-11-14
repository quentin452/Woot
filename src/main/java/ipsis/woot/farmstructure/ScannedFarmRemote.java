package ipsis.woot.farmstructure;

import net.minecraft.util.math.ChunkCoordinates;

import java.util.ArrayList;
import java.util.List;

public class ScannedFarmRemote {

    private ChunkCoordinates powerPos;
    private ChunkCoordinates exportPos;
    private ChunkCoordinates importPos;

    public static boolean isEqual(ScannedFarmRemote a, ScannedFarmRemote b) {

        if (a == null || b == null)
            return false;

        if (a.powerPos != null && b.powerPos != null) {
            if (!a.powerPos.equals(b.powerPos))
                return false;
        } else {
            if (a.powerPos == null && b.powerPos != null)
                return false;
            if (b.powerPos == null && a.powerPos != null)
                return false;
        }

        if (a.exportPos != null && b.exportPos != null) {
            if (!a.exportPos.equals(b.exportPos))
                return false;
        } else {
            if (a.exportPos == null && b.exportPos != null)
                return false;
            if (b.exportPos == null && a.exportPos != null)
                return false;
        }

        if (a.importPos != null && b.importPos != null) {
            if (!a.importPos.equals(b.importPos))
                return false;
        } else {
            if (a.importPos == null && b.importPos != null)
                return false;
            if (b.importPos == null && a.importPos != null)
                return false;
        }


        return true;
    }

    public boolean isValid() {

        final int IMPORT_Y_OFFSET = 1;
        final int EXPORT_Y_OFFSET = 2;

        if (powerPos == null || exportPos == null || importPos == null)
            return false;

        int y = powerPos.getY();

        if (importPos.getY() != y - IMPORT_Y_OFFSET)
            return false;

        if (exportPos.getY() != y - EXPORT_Y_OFFSET)
            return false;

        return true;
    }

    public boolean hasPower() {

        return this.powerPos != null;
    }

    public boolean hasExport() {

        return this.exportPos != null;
    }

    public boolean hasImport() {

        return this.importPos != null;
    }

    public void setPowerPos(ChunkCoordinates pos) {

        this.powerPos = pos;
    }

    public ChunkCoordinates getPowerPos() {

        return this.powerPos;
    }

    public void setExportPos(ChunkCoordinates pos) {

        this.exportPos = pos;
    }

    public ChunkCoordinates getExportPos() {

        return this.exportPos;
    }

    public void setImportPos(ChunkCoordinates pos) {

        this.importPos = pos;
    }

    public ChunkCoordinates getImportPos() {

        return this.importPos;
    }

    public List<ChunkCoordinates> getBlocks() {

        List<ChunkCoordinates> blocks = new ArrayList<>();
        if (powerPos != null)
            blocks.add(powerPos);
        if (exportPos != null)
            blocks.add(exportPos);
        if (importPos != null)
            blocks.add(importPos);

        return blocks;
    }
}
