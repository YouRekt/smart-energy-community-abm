import type { defaultValues } from '@/routes/config/-components/config-form/schema';

const exampleConfig: typeof defaultValues = {
	seed: 12345,
	strategyName: 'Balanced',
	predictionModelConfig: {
		name: 'MovingAverage',
		minBatteryChargeThreshold: 20,
		productionSafetyFactor: 90,
		windowSize: 20,
	},
	batteryConfig: {
		capacity: 2500.0,
		startingCharge: 25,
		isPercentage: true,
	},
	energySourcesConfigs: [
		{
			agentName: 'Community_Solar_Farm_East',
			period: 144,
			maxOutputPower: 150.0,
			peakTick: 72,
			stdDev: 12.0,
			variation: 15,
		},
		{
			agentName: 'Community_Solar_Farm_West',
			period: 144,
			maxOutputPower: 120.0,
			peakTick: 80,
			stdDev: 15.0,
			variation: 20,
		},
	],
	householdConfigs: [
		// Households 1-10: Washer + Dishwasher
		...Array.from({ length: 10 }, (_, i) => ({
			householdName: `Household_${String(i + 1).padStart(2, '0')}`,
			applianceConfigs: [
				{
					applianceName: 'Washer',
					householdName: `Household_${String(i + 1).padStart(2, '0')}`,
					tasks: [
						{
							taskName: 'HeavyWash',
							humanActivationChance: 5,
							period: 144,
							postponable: true,
							duration: 9,
							energyPerTick: 1.2,
							taskId: (i + 1) * 100 + 1,
						},
					],
				},
				{
					applianceName: 'Dishwasher',
					householdName: `Household_${String(i + 1).padStart(2, '0')}`,
					tasks: [
						{
							taskName: 'EcoCycle',
							humanActivationChance: 10,
							period: 144,
							postponable: true,
							duration: 12,
							energyPerTick: 0.8,
							taskId: (i + 1) * 100 + 2,
						},
					],
				},
			],
		})),
		// Households 11-15: EV_Charger + Washer
		...Array.from({ length: 5 }, (_, i) => ({
			householdName: `Household_${i + 11}`,
			applianceConfigs: [
				{
					applianceName: 'EV_Charger',
					householdName: `Household_${i + 11}`,
					tasks: [
						{
							taskName: 'ChargeVehicle',
							humanActivationChance: 20,
							period: 144,
							postponable: true,
							duration: 18,
							energyPerTick: 5.0,
							taskId: (i + 11) * 100 + 1,
						},
					],
				},
				{
					applianceName: 'Washer',
					householdName: `Household_${i + 11}`,
					tasks: [
						{
							taskName: 'QuickWash',
							humanActivationChance: 10,
							period: 72,
							postponable: true,
							duration: 4,
							energyPerTick: 1.0,
							taskId: (i + 11) * 100 + 2,
						},
					],
				},
			],
		})),
		// Households 16-17: GamingPC
		...Array.from({ length: 2 }, (_, i) => ({
			householdName: `Household_${i + 16}`,
			applianceConfigs: [
				{
					applianceName: 'GamingPC',
					householdName: `Household_${i + 16}`,
					tasks: [
						{
							taskName: 'GamingSession',
							humanActivationChance: 30, // 0.3 * 100
							period: 72,
							postponable: false,
							duration: 12,
							energyPerTick: 0.4,
							taskId: (i + 16) * 100 + 1,
						},
					],
				},
			],
		})),
		// Households 18-19: HomeCinema
		...Array.from({ length: 2 }, (_, i) => ({
			householdName: `Household_${i + 18}`,
			applianceConfigs: [
				{
					applianceName: 'HomeCinema',
					householdName: `Household_${i + 18}`,
					tasks: [
						{
							taskName: 'MovieMarathon',
							humanActivationChance: 20, // 0.2 * 100
							period: 144,
							postponable: true,
							duration: 18,
							energyPerTick: 0.3,
							taskId: (i + 18) * 100 + 1,
						},
					],
				},
			],
		})),
		// Household 20: Dehumidifier
		{
			householdName: 'Household_20',
			applianceConfigs: [
				{
					applianceName: 'Dehumidifier',
					householdName: 'Household_20',
					tasks: [
						{
							taskName: 'DryAir',
							humanActivationChance: 50, // 0.5 * 100
							period: 36,
							postponable: true,
							duration: 6,
							energyPerTick: 0.6,
							taskId: 2001,
						},
					],
				},
			],
		},
	],
};

export { exampleConfig };
