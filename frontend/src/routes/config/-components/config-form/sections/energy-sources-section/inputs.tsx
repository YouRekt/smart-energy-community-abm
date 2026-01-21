import { Field, FieldError, FieldLabel } from '@/components/ui/field';
import { Input } from '@/components/ui/input';
import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { energySourcesConfigSchema } from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

export function EnergySourceAgentNameInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.agentName>
		>();
	return (
		<Field>
			<FieldLabel>Agent Name</FieldLabel>
			<Input
				value={field.state.value}
				onChange={(e) => field.handleChange(e.target.value)}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}

export function EnergySourcePeriodInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.period>
		>();
	return (
		<Field>
			<FieldLabel>Period</FieldLabel>
			<Input
				type='number'
				value={field.state.value}
				onChange={(e) => field.handleChange(Number(e.target.value))}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}

export function EnergySourceMaxOutputPowerInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.maxOutputPower>
		>();
	return (
		<Field>
			<FieldLabel>Max Power</FieldLabel>
			<Input
				type='number'
				value={field.state.value}
				onChange={(e) => field.handleChange(Number(e.target.value))}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}

export function EnergySourcePeakTickInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.peakTick>
		>();
	return (
		<Field>
			<FieldLabel>Peak Tick</FieldLabel>
			<Input
				type='number'
				value={field.state.value}
				onChange={(e) => field.handleChange(Number(e.target.value))}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}

export function EnergySourceStdDevInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.stdDev>
		>();
	return (
		<Field>
			<FieldLabel>Std Dev</FieldLabel>
			<Input
				type='number'
				value={field.state.value}
				onChange={(e) => field.handleChange(Number(e.target.value))}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}

export function EnergySourceVariationInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.variation>
		>();
	return (
		<Field>
			<FieldLabel>Variation</FieldLabel>
			<Input
				type='number'
				value={field.state.value}
				onChange={(e) => field.handleChange(Number(e.target.value))}
			/>
			{field.state.meta.errors.length > 0 && (
				<FieldError errors={field.state.meta.errors} />
			)}
		</Field>
	);
}
