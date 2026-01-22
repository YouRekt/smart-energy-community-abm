import {
	CapacityInput,
	IsPercentageInput,
	StartingChargeInput,
} from '@/routes/config/-components/config-form/sections/battery-section/index';
import {
	EnergySourceAgentNameInput,
	EnergySourceMaxOutputPowerInput,
	EnergySourcePeakTickInput,
	EnergySourcePeriodInput,
	EnergySourceStdDevInput,
	EnergySourceVariationInput,
} from '@/routes/config/-components/config-form/sections/energy-sources-section/index';
import {
	ApplianceNameInput,
	HouseholdNameInput,
	TaskDurationInput,
	TaskEnergyPerTickInput,
	TaskHumanActivationChanceInput,
	TaskNameInput,
	TaskPeriodInput,
	TaskPostponableInput,
} from '@/routes/config/-components/config-form/sections/households-section/index';
import {
	MinBatteryChargeThresholdInput,
	PredictionModelSelect,
	ProductionSafetyFactorInput,
	WindowSizeInput,
} from '@/routes/config/-components/config-form/sections/prediction-model-section/index';
import SeedInput from '@/routes/config/-components/config-form/sections/strategy-section/seed-input';
import { StrategySelect } from '@/routes/config/-components/config-form/sections/strategy-section/strategy-select';
import { createFormHook, createFormHookContexts } from '@tanstack/react-form';

const { fieldContext, formContext, useFieldContext } = createFormHookContexts();

const { useAppForm, withForm } = createFormHook({
	fieldComponents: {
		StrategySelect,
		PredictionModelSelect,
		MinBatteryChargeThresholdInput,
		ProductionSafetyFactorInput,
		WindowSizeInput,
		CapacityInput,
		StartingChargeInput,
		IsPercentageInput,
		EnergySourceAgentNameInput,
		EnergySourcePeriodInput,
		EnergySourceMaxOutputPowerInput,
		EnergySourcePeakTickInput,
		EnergySourceStdDevInput,
		EnergySourceVariationInput,
		HouseholdNameInput,
		ApplianceNameInput,
		TaskNameInput,
		TaskHumanActivationChanceInput,
		TaskDurationInput,
		TaskPeriodInput,
		TaskEnergyPerTickInput,
		TaskPostponableInput,
		SeedInput,
	},
	formComponents: {},
	fieldContext,
	formContext,
});

export {
	useFieldContext as useConfigFieldContext,
	useAppForm as useConfigForm,
	withForm as withConfigForm,
};
