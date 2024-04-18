package adjustabledmods;

import adjustabledmods.ui.RefitButtonUI;
import com.fs.starfarer.api.BaseModPlugin;
import lunalib.lunaRefit.LunaRefitManager;

public class ModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        LunaRefitManager.addRefitButton(new RefitButtonUI());
    }
}
