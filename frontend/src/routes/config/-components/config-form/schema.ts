import * as z from 'zod';

const predictionModelConfigSchema = z.object({
	name: z
		.enum(['MovingAverage'], {
			message: 'Please select a valid prediction model',
		})
		.default('MovingAverage'),
	minBatteryChargeThreshold: z.coerce
		.number<number>()
		.min(0, { message: 'Minimum charge threshold must be at least 0 %' })
		.max(100, {
			message: 'Minimum charge threshold must be at most 100 %',
		}),
	productionSafetyFactor: z.coerce
		.number<number>()
		.min(0, { message: 'Production safety factor must be at least 0 %' })
		.max(100, {
			message: 'Production safety factor must be at most 100 %',
		}),
	windowSize: z.coerce
		.number<number>()
		.int({ message: 'Window size must be an integer' })
		.positive({ message: 'Window size must be positive' }),
});

const predictionModelConfigSchemaTransformed =
	predictionModelConfigSchema.transform((data) => {
		return {
			...data,
			minBatteryChargeThreshold: data.minBatteryChargeThreshold / 100,
			productionSafetyFactor: data.productionSafetyFactor / 100,
		};
	});

const batteryConfigSchema = z
	.object({
		capacity: z.coerce
			.number<number>()
			.positive({ message: 'Capacity must be positive' }),
		startingCharge: z.coerce
			.number<number>()
			.min(0, { message: 'Starting charge cannot be negative' }),
		isPercentage: z.boolean(),
	})
	.superRefine((data, ctx) => {
		if (data.isPercentage) {
			if (data.startingCharge > 100) {
				ctx.addIssue({
					code: 'too_big',
					maximum: 100,
					origin: 'number',
					inclusive: true,
					input: data.startingCharge,
					message:
						'Starting charge percentage must be between 0 and 100 %',
					path: ['startingCharge'],
				});
			}
		} else {
			if (data.startingCharge > data.capacity) {
				ctx.addIssue({
					code: 'too_big',
					maximum: data.capacity,
					origin: 'number',
					inclusive: true,
					input: data.startingCharge,
					message: "Starting charge can't be greater than capacity",
					path: ['startingCharge'],
				});
			}
		}
	});

const batteryConfigSchemaTransformed = batteryConfigSchema.transform((data) => {
	if (data.isPercentage) {
		return {
			...data,
			startingCharge: data.startingCharge / 100,
		};
	}
	return data;
});

const energySourcesConfigSchema = z.object({
	agentName: z
		.string()
		.min(3, { message: 'Agent name must be at least 3 characters' }),
	period: z.coerce
		.number<number>()
		.int({ message: 'Period must be an integer' })
		.positive({ message: 'Period must be positive' }),
	maxOutputPower: z.coerce
		.number<number>()
		.positive({ message: 'Max output power must be positive' }),
	peakTick: z.coerce
		.number<number>()
		.int({ message: 'Peak tick must be an integer' })
		.positive({ message: 'Peak tick must be positive' }),
	stdDev: z.coerce
		.number<number>()
		.positive({ message: 'Standard deviation must be positive' }),
	variation: z.coerce
		.number<number>()
		.gt(0, { message: 'Variation must be greater than 0 %' })
		.max(100, { message: 'Variation must be at most 100 %' }),
});

const energySourcesConfigSchemaTransformed =
	energySourcesConfigSchema.transform((data) => {
		return {
			...data,
			variation: data.variation / 100,
		};
	});

const applianceTaskSchema = z.object({
	taskName: z.string().min(1, { message: 'Task name required' }),
	humanActivationChance: z.coerce
		.number<number>()
		.min(0, { message: 'Chance must be at least 0 %' })
		.max(100, { message: 'Chance must be at most 100 %' }),
	period: z.coerce
		.number<number>()
		.int({ message: 'Period must be an integer' }),
	postponable: z.boolean(),
	duration: z.coerce
		.number<number>()
		.int({ message: 'Duration must be an integer' }),
	energyPerTick: z.coerce
		.number<number>()
		.positive({ message: 'Energy per tick must be positive' }),
	taskId: z.coerce.number<number>().int(),
});

const applianceTaskSchemaTransformed = applianceTaskSchema.transform((data) => {
	return {
		...data,
		humanActivationChance: data.humanActivationChance / 100,
	};
});

const applianceConfigSchema = z.object({
	applianceName: z
		.string()
		.min(3, { message: 'Appliance name must be at least 3 characters' }),
	householdName: z.string().optional(), // will be by a transform
	tasks: z.array(applianceTaskSchemaTransformed).min(1, {
		message: 'At least one task required',
	}),
});

const householdConfigSchema = z.object({
	householdName: z
		.string()
		.min(3, { message: 'Household name must be at least 3 characters' }),
	applianceConfigs: z.array(applianceConfigSchema).min(1, {
		message: 'At least one appliance required',
	}),
});

const householdConfigSchemaTransformed = householdConfigSchema.transform(
	(data) => {
		return {
			...data,
			applianceConfigs: data.applianceConfigs.map((appliance) => {
				return {
					...appliance,
					hasHouseholdName: data.householdName,
				};
			}),
		};
	},
);

const formSchema = z
	.object({
		strategyName: z
			.enum(
				[
					'Balanced',
					'EnergyVolume',
					'GreenScoreFirst',
					'ReservationFirst',
				],
				{
					message: 'Please select a valid strategy',
				},
			)
			.default('Balanced'),
		predictionModelConfig: predictionModelConfigSchemaTransformed,
		batteryConfig: batteryConfigSchemaTransformed,
		energySourcesConfigs: z
			.array(energySourcesConfigSchemaTransformed)
			.min(1, {
				message: 'At least one energy source required',
			}),
		householdConfigs: z.array(householdConfigSchemaTransformed).min(1, {
			message: 'At least one household required',
		}),
	})
	.superRefine((data, ctx) => {
		const taskIds = new Set<number>();
		const householdNames = new Set<string>();
		data.householdConfigs.forEach((household, householdIndex) => {
			if (householdNames.has(household.householdName)) {
				ctx.addIssue({
					code: 'custom',
					message: 'Household name must be unique',
					path: ['householdConfigs', householdIndex, 'householdName'],
				});
			}
			householdNames.add(household.householdName);

			household.applianceConfigs.forEach((appliance, applianceIndex) => {
				appliance.tasks.forEach((task, taskIndex) => {
					if (task.taskId !== undefined) {
						if (taskIds.has(task.taskId)) {
							ctx.addIssue({
								code: 'custom',
								message: 'Task ID must be unique',
								path: [
									'householdConfigs',
									householdIndex,
									'applianceConfigs',
									applianceIndex,
									'tasks',
									taskIndex,
									'taskId',
								],
							});
						} else {
							taskIds.add(task.taskId);
						}
					}
				});
			});
		});
	});

const defaultValues: z.input<typeof formSchema> = {
	strategyName: 'Balanced',
	predictionModelConfig: {
		name: 'MovingAverage',
		minBatteryChargeThreshold: 0,
		productionSafetyFactor: 0,
		windowSize: 0,
	},
	batteryConfig: {
		capacity: 0,
		startingCharge: 0,
		isPercentage: false,
	},
	energySourcesConfigs: [],
	householdConfigs: [],
};

const strategyDefaultValues: z.input<typeof formSchema.shape.strategyName> =
	'Balanced';

const predictionModelDefaultValues: z.input<
	typeof predictionModelConfigSchema
> = {
	name: 'MovingAverage',
	minBatteryChargeThreshold: 0,
	productionSafetyFactor: 0,
	windowSize: 0,
};

const batteryDefaultValues: z.input<typeof batteryConfigSchema> = {
	capacity: 0,
	startingCharge: 0,
	isPercentage: false,
};

const energySourceDefaultValues: z.input<typeof energySourcesConfigSchema> = {
	agentName: '',
	period: 0,
	maxOutputPower: 0,
	peakTick: 0,
	stdDev: 0,
	variation: 0,
};

const householdDefaultValues: z.input<typeof householdConfigSchema> = {
	householdName: '',
	applianceConfigs: [],
};

const applianceDefaultValues: z.input<typeof applianceConfigSchema> = {
	applianceName: '',
	householdName: '', // not in form
	tasks: [],
};

const applianceTaskDefaultValues: z.input<typeof applianceTaskSchema> = {
	taskName: '',
	humanActivationChance: 0,
	period: 0,
	postponable: false,
	duration: 0,
	energyPerTick: 0,
	taskId: 0,
};

export {
	applianceConfigSchema,
	applianceDefaultValues,
	applianceTaskDefaultValues,
	applianceTaskSchema,
	batteryConfigSchema,
	batteryDefaultValues,
	defaultValues,
	energySourceDefaultValues,
	energySourcesConfigSchema,
	formSchema,
	householdConfigSchema,
	householdDefaultValues,
	predictionModelConfigSchema,
	predictionModelDefaultValues,
	strategyDefaultValues,
};
