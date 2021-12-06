package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;

public class AssetManager {
    Map<String, Integer> assetIdentifier = new HashMap<String, Integer>();
    List<Asset> assets = new ArrayList<Asset>();
    public void addAsset(String name, Flat src){
        int index = assets.size();
        src.name = name+"_f";
        if(flatID(name)!=-1){
            assets.set(flatID(name),src);
        }else{
        assets.add(src);
        assetIdentifier.put(src.name, index);
        }
	}

    public List<Asset> getAssets(){
        return assets;
    }

    public void addAsset(String name, Wall src){
        int index = assets.size();
        src.name = name+"_w";
        if(wallID(name)!=-1){
            assets.set(wallID(name),src);
        }else{
        assets.add(src);
        assetIdentifier.put(src.name, index);
        }
	}

    public Wall getWall(int i){
        return (Wall)assets.get(i);
    }
    public Wall getWall(String name){
        int i = assetIdentifier.get(name+"_w");
        return (Wall)assets.get(i);
    }
    public Flat getFlat(int i){
        return (Flat)assets.get(i);
    }
    public Flat getFlat(String name){
        int i = assetIdentifier.get(name+"_f");
        return (Flat)assets.get(i);
    }

    public int wallID(String name){
        if(assetIdentifier.get(name+"_w")!=null)
            return assetIdentifier.get(name+"_w");
        return -1;
    }
    public int flatID(String name){
        if(assetIdentifier.get(name+"_f")!=null)
            return assetIdentifier.get(name+"_f");
        return -1;
    }
}

class Asset{
	String name;
}
class Flat extends Asset{
    Flat(BufferedImage src){
        this.src = src;
    }
    BufferedImage src;
}
class Wall extends Asset{
	Shear top_left;
    Shear top_right;
    Shear bottom_right;
    Shear bottom_left;
}
class Shear{
    Shear(ShearReturn ret){
        this.src = ret.image;
        this.offset1 = ret.offset1;
        this.offset2 = ret.offset2;
    }
    BufferedImage src;
    int offset1;
    int offset2;
}