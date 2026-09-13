package snownee.fruits.datagen.guide;

import java.util.Optional;

import com.klikli_dev.modonomicon.api.datagen.book.condition.BookConditionModel;
import com.klikli_dev.modonomicon.book.conditions.BookCondition;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import snownee.fruits.guide.BookFFModuleLoadedCondition;

public class BookFFModuleLoadedConditionModel extends BookConditionModel<BookFFModuleLoadedConditionModel> {

	private String moduleId;
	private boolean inverted;

	protected BookFFModuleLoadedConditionModel() {
		super(BookFFModuleLoadedCondition.ID);
	}

	public static BookFFModuleLoadedConditionModel create() {
		return new BookFFModuleLoadedConditionModel();
	}

	@Override
	public BookCondition toBookCondition(HolderLookup.Provider provider) {
		return new BookFFModuleLoadedCondition(Optional.ofNullable(tooltipComponent()), Identifier.parse(moduleId), inverted);
	}

	public String getModuleId() {
		return moduleId;
	}

	public boolean isInverted() {
		return inverted;
	}

	public BookFFModuleLoadedConditionModel withModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}

	public BookFFModuleLoadedConditionModel withInverted(boolean inverted) {
		this.inverted = inverted;
		return this;
	}

}