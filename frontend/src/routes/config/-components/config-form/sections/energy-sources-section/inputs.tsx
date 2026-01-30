import { NumberInput } from '@/components/number-input';
import {
	Field,
	FieldDescription,
	FieldError,
	FieldLabel,
} from '@/components/ui/field';
import { Input } from '@/components/ui/input';

import { useConfigFieldContext } from '@/routes/config/-components/config-form/form-context';
import { energySourcesConfigSchema } from '@/routes/config/-components/config-form/schema';
import { z } from 'zod';

export function EnergySourceAgentNameInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.agentName>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel>Agent Name</FieldLabel>
			<Input
				value={field.state.value}
				onChange={(e) => field.handleChange(e.target.value)}
			/>
			<FieldDescription>The name of the energy source.</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function EnergySourcePeriodInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.period>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Period</FieldLabel>
			<NumberInput
				id={field.name}
				value={field.state.value}
				min={0}
				onChange={(e) =>
					field.handleChange(
						energySourcesConfigSchema.shape.period.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>
				The period of the energy source (relative to the configured tick
				unit).
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function EnergySourceMaxOutputPowerInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.maxOutputPower>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Max Output Power</FieldLabel>
			<NumberInput
				id={field.name}
				step={0.01}
				value={field.state.value}
				min={0}
				addon={{
					align: 'end',
					content: 'W',
				}}
				onChange={(e) =>
					field.handleChange(
						energySourcesConfigSchema.shape.maxOutputPower.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>
				The max output power of the energy source.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function EnergySourcePeakTickInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.peakTick>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Peak Tick</FieldLabel>
			<NumberInput
				id={field.name}
				value={field.state.value}
				onChange={(e) =>
					field.handleChange(
						energySourcesConfigSchema.shape.peakTick.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>
				The peak time of the energy source in the defined period
				(relative to the configured tick unit).
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function EnergySourceStdDevInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.stdDev>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Standard Deviation</FieldLabel>
			<NumberInput
				id={field.name}
				step={0.01}
				value={field.state.value}
				onChange={(e) =>
					field.handleChange(
						energySourcesConfigSchema.shape.stdDev.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>
				The standard deviation of the energy source curve.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}

export function EnergySourceVariationInput() {
	const field =
		useConfigFieldContext<
			z.input<typeof energySourcesConfigSchema.shape.variation>
		>();

	const isInvalid = field.state.meta.isTouched && !field.state.meta.isValid;

	return (
		<Field data-invalid={isInvalid}>
			<FieldLabel htmlFor={field.name}>Variation</FieldLabel>
			<NumberInput
				id={field.name}
				step={0.01}
				min={0}
				max={100}
				addon={{
					align: 'end',
					content: '%',
				}}
				value={field.state.value}
				onChange={(e) =>
					field.handleChange(
						energySourcesConfigSchema.shape.variation.safeParse(
							e.target.value,
						).data ?? field.state.value,
					)
				}
			/>
			<FieldDescription>
				The variation (%) of the energy source curve.
			</FieldDescription>
			{isInvalid && <FieldError errors={field.state.meta.errors} />}
		</Field>
	);
}
