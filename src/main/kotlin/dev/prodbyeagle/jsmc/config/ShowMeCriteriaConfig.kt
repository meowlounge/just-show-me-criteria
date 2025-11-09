package dev.prodbyeagle.jsmc.config

import com.google.gson.annotations.SerializedName

data class ShowMeCriteriaConfig(
    @SerializedName("enabled")
    var enabled: Boolean = true,
    @SerializedName("pinned_advancement")
    var pinnedAdvancement: String = "minecraft:story/mine_diamond",
    @SerializedName("hide_when_completed")
    var hideWhenCompleted: Boolean = false,
    @SerializedName("criteria_max_visible")
    var criteriaMaxVisible: Int = 6,
    @SerializedName("hud")
    var hud: HudConfig = HudConfig(),
    @SerializedName("style")
    var style: StyleConfig = StyleConfig()
) {

    data class HudConfig(
        @SerializedName("anchor")
        var anchor: Anchor = Anchor.TOP_RIGHT,
        @SerializedName("offset_x")
        var offsetX: Int = 16,
        @SerializedName("offset_y")
        var offsetY: Int = 16,
        @SerializedName("scale")
        var scale: Double = 1.0
    ) {
        enum class Anchor {
            TOP_LEFT,
            TOP_RIGHT,
            BOTTOM_LEFT,
            BOTTOM_RIGHT
        }
    }

    data class StyleConfig(
        @SerializedName("header_color")
        var headerColor: String? = null,
        @SerializedName("description_color")
        var descriptionColor: String? = null,
        @SerializedName("progress_text_color")
        var progressTextColor: String? = null
    )
}
