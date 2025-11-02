package dev.prodbyeagle.pinnedAdvancements.config

import com.google.gson.annotations.SerializedName

data class PinnedAdvancementsConfig(
    @SerializedName("enabled")
    var enabled: Boolean = true,
    @SerializedName("pinned_advancement")
    var pinnedAdvancement: String = "minecraft:story/mine_diamond",
    @SerializedName("hide_when_completed")
    var hideWhenCompleted: Boolean = false,
    @SerializedName("show_title")
    var showTitle: Boolean = true,
    @SerializedName("show_description")
    var showDescription: Boolean = true,
    @SerializedName("show_progress_bar")
    var showProgressBar: Boolean = true,
    @SerializedName("show_progress_text")
    var showProgressText: Boolean = true,
    @SerializedName("show_icon")
    var showIcon: Boolean = true,
    @SerializedName("custom_title")
    var customTitle: String? = null,
    @SerializedName("custom_description")
    var customDescription: List<String>? = null,
    @SerializedName("custom_icon")
    var customIcon: IconConfig? = null,
    @SerializedName("hud")
    var hud: HudConfig = HudConfig(),
    @SerializedName("style")
    var style: StyleConfig = StyleConfig()
) {

    data class IconConfig(
        @SerializedName("item")
        var item: String? = null,
        @SerializedName("count")
        var count: Int = 1
    )

    data class HudConfig(
        @SerializedName("anchor")
        var anchor: Anchor = Anchor.TOP_RIGHT,
        @SerializedName("offset_x")
        var offsetX: Int = 16,
        @SerializedName("offset_y")
        var offsetY: Int = 16
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
        @SerializedName("title_color")
        var titleColor: String? = null,
        @SerializedName("description_color")
        var descriptionColor: String? = null,
        @SerializedName("progress_text_color")
        var progressTextColor: String? = null,
        @SerializedName("progress_bar_color")
        var progressBarColor: String? = null,
        @SerializedName("progress_bar_background_color")
        var progressBarBackgroundColor: String? = null
    )
}
