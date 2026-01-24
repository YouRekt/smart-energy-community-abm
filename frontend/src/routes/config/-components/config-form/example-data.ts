import type { defaultValues } from '@/routes/config/-components/config-form/schema';

const exampleConfig: typeof defaultValues = {
	seed: 42,
	tickConfig: {
		tickUnit: 'minute',
		tickAmount: 15,
	},
	strategyName: 'Balanced',
	predictionModelConfig: {
		name: 'MovingAverage',
		minBatteryChargeThreshold: 20,
		productionSafetyFactor: 90,
		windowSize: 60,
	},
	batteryConfig: {
		capacity: 250.0,
		startingCharge: 50,
		isPercentage: true,
	},
	energySourcesConfigs: [
		{
			agentName: 'Community_Solar_Farm',
			period: 1440,
			maxOutputPower: 80000.0,
			peakTick: 720,
			stdDev: 120.0,
			variation: 15,
		},
		{
			agentName: 'Small_Wind_Turbine',
			period: 1440,
			maxOutputPower: 30000.0,
			peakTick: 1080,
			stdDev: 240.0,
			variation: 30,
		},
	],
	householdConfigs: [
		{
			householdName: 'HH_01_EV_Commuter',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_01_EV_Commuter',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 400.0,
							taskId: 100,
						},
					],
				},
				{
					applianceName: 'EV_Charger',
					householdName: 'HH_01_EV_Commuter',
					tasks: [
						{
							taskName: 'Charge_Level2',
							humanActivationChance: 90,
							period: 1440,
							postponable: true,
							duration: 240,
							energyPerTick: 7000.0,
							taskId: 101,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_02_EV_Commuter',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_02_EV_Commuter',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 400.0,
							taskId: 200,
						},
					],
				},
				{
					applianceName: 'EV_Charger',
					householdName: 'HH_02_EV_Commuter',
					tasks: [
						{
							taskName: 'Charge_Level2',
							humanActivationChance: 90,
							period: 1440,
							postponable: true,
							duration: 240,
							energyPerTick: 7000.0,
							taskId: 201,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_03_EV_Commuter',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_03_EV_Commuter',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 400.0,
							taskId: 300,
						},
					],
				},
				{
					applianceName: 'EV_Charger',
					householdName: 'HH_03_EV_Commuter',
					tasks: [
						{
							taskName: 'Charge_Level2',
							humanActivationChance: 90,
							period: 1440,
							postponable: true,
							duration: 240,
							energyPerTick: 7000.0,
							taskId: 301,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_04_EV_Commuter',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_04_EV_Commuter',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 400.0,
							taskId: 400,
						},
					],
				},
				{
					applianceName: 'EV_Charger',
					householdName: 'HH_04_EV_Commuter',
					tasks: [
						{
							taskName: 'Charge_Level2',
							humanActivationChance: 90,
							period: 1440,
							postponable: true,
							duration: 240,
							energyPerTick: 7000.0,
							taskId: 401,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_05_Family_Large',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_05_Family_Large',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 500.0,
							taskId: 500,
						},
					],
				},
				{
					applianceName: 'HeatPump',
					householdName: 'HH_05_Family_Large',
					tasks: [
						{
							taskName: 'Heating',
							humanActivationChance: 100,
							period: 60,
							postponable: false,
							duration: 15,
							energyPerTick: 3500.0,
							taskId: 501,
						},
					],
				},
				{
					applianceName: 'Oven',
					householdName: 'HH_05_Family_Large',
					tasks: [
						{
							taskName: 'Dinner',
							humanActivationChance: 60,
							period: 1440,
							postponable: false,
							duration: 90,
							energyPerTick: 2400.0,
							taskId: 502,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_06_Family_Large',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_06_Family_Large',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 500.0,
							taskId: 600,
						},
					],
				},
				{
					applianceName: 'HeatPump',
					householdName: 'HH_06_Family_Large',
					tasks: [
						{
							taskName: 'Heating',
							humanActivationChance: 100,
							period: 60,
							postponable: false,
							duration: 15,
							energyPerTick: 3500.0,
							taskId: 601,
						},
					],
				},
				{
					applianceName: 'Stove',
					householdName: 'HH_06_Family_Large',
					tasks: [
						{
							taskName: 'Cooking',
							humanActivationChance: 70,
							period: 1440,
							postponable: false,
							duration: 45,
							energyPerTick: 3000.0,
							taskId: 602,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_07_Family_Standard',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_07_Family_Standard',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 350.0,
							taskId: 700,
						},
					],
				},
				{
					applianceName: 'WashingMachine',
					householdName: 'HH_07_Family_Standard',
					tasks: [
						{
							taskName: 'DailyWash',
							humanActivationChance: 50,
							period: 1440,
							postponable: true,
							duration: 90,
							energyPerTick: 2200.0,
							taskId: 701,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_08_Family_Standard',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_08_Family_Standard',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 350.0,
							taskId: 800,
						},
					],
				},
				{
					applianceName: 'Dryer',
					householdName: 'HH_08_Family_Standard',
					tasks: [
						{
							taskName: 'DryClothes',
							humanActivationChance: 50,
							period: 1440,
							postponable: true,
							duration: 60,
							energyPerTick: 3000.0,
							taskId: 801,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_09_Remote_Worker',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_09_Remote_Worker',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 300.0,
							taskId: 900,
						},
					],
				},
				{
					applianceName: 'WorkPC',
					householdName: 'HH_09_Remote_Worker',
					tasks: [
						{
							taskName: 'WorkDay',
							humanActivationChance: 95,
							period: 1440,
							postponable: false,
							duration: 480,
							energyPerTick: 400.0,
							taskId: 901,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_10_Remote_Worker',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_10_Remote_Worker',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 300.0,
							taskId: 1000,
						},
					],
				},
				{
					applianceName: 'WorkPC',
					householdName: 'HH_10_Remote_Worker',
					tasks: [
						{
							taskName: 'WorkDay',
							humanActivationChance: 95,
							period: 1440,
							postponable: false,
							duration: 480,
							energyPerTick: 400.0,
							taskId: 1001,
						},
					],
				},
				{
					applianceName: 'Heater',
					householdName: 'HH_10_Remote_Worker',
					tasks: [
						{
							taskName: 'OfficeWarmth',
							humanActivationChance: 60,
							period: 240,
							postponable: true,
							duration: 60,
							energyPerTick: 1500.0,
							taskId: 1002,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_11_Remote_Worker',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_11_Remote_Worker',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 300.0,
							taskId: 1100,
						},
					],
				},
				{
					applianceName: 'WorkPC',
					householdName: 'HH_11_Remote_Worker',
					tasks: [
						{
							taskName: 'WorkDay',
							humanActivationChance: 95,
							period: 1440,
							postponable: false,
							duration: 480,
							energyPerTick: 400.0,
							taskId: 1101,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_12_Remote_Worker',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_12_Remote_Worker',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 300.0,
							taskId: 1200,
						},
					],
				},
				{
					applianceName: 'WorkPC',
					householdName: 'HH_12_Remote_Worker',
					tasks: [
						{
							taskName: 'WorkDay',
							humanActivationChance: 95,
							period: 1440,
							postponable: false,
							duration: 480,
							energyPerTick: 400.0,
							taskId: 1201,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_13_Retired',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_13_Retired',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 250.0,
							taskId: 1300,
						},
					],
				},
				{
					applianceName: 'Television',
					householdName: 'HH_13_Retired',
					tasks: [
						{
							taskName: 'EveningTV',
							humanActivationChance: 80,
							period: 1440,
							postponable: true,
							duration: 240,
							energyPerTick: 150.0,
							taskId: 1301,
						},
					],
				},
				{
					applianceName: 'Dishwasher',
					householdName: 'HH_13_Retired',
					tasks: [
						{
							taskName: 'EcoCycle',
							humanActivationChance: 50,
							period: 1440,
							postponable: true,
							duration: 120,
							energyPerTick: 1800.0,
							taskId: 1302,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_14_Retired',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_14_Retired',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 250.0,
							taskId: 1400,
						},
					],
				},
				{
					applianceName: 'Television',
					householdName: 'HH_14_Retired',
					tasks: [
						{
							taskName: 'EveningTV',
							humanActivationChance: 80,
							period: 1440,
							postponable: true,
							duration: 240,
							energyPerTick: 150.0,
							taskId: 1401,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_15_Apartment',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_15_Apartment',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 150.0,
							taskId: 1500,
						},
					],
				},
				{
					applianceName: 'GamingPC',
					householdName: 'HH_15_Apartment',
					tasks: [
						{
							taskName: 'Gaming',
							humanActivationChance: 60,
							period: 1440,
							postponable: false,
							duration: 240,
							energyPerTick: 500.0,
							taskId: 1501,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_16_Apartment',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_16_Apartment',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 150.0,
							taskId: 1600,
						},
					],
				},
				{
					applianceName: 'GamingPC',
					householdName: 'HH_16_Apartment',
					tasks: [
						{
							taskName: 'Gaming',
							humanActivationChance: 60,
							period: 1440,
							postponable: false,
							duration: 240,
							energyPerTick: 500.0,
							taskId: 1601,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_17_Apartment',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_17_Apartment',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 150.0,
							taskId: 1700,
						},
					],
				},
				{
					applianceName: 'Microwave',
					householdName: 'HH_17_Apartment',
					tasks: [
						{
							taskName: 'MealPrep',
							humanActivationChance: 90,
							period: 720,
							postponable: false,
							duration: 5,
							energyPerTick: 1200.0,
							taskId: 1701,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_18_Apartment',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_18_Apartment',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 150.0,
							taskId: 1800,
						},
					],
				},
				{
					applianceName: 'AirFryer',
					householdName: 'HH_18_Apartment',
					tasks: [
						{
							taskName: 'Cooking',
							humanActivationChance: 75,
							period: 1440,
							postponable: false,
							duration: 30,
							energyPerTick: 1500.0,
							taskId: 1801,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_19_Apartment',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_19_Apartment',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 150.0,
							taskId: 1900,
						},
					],
				},
				{
					applianceName: 'Laptop',
					householdName: 'HH_19_Apartment',
					tasks: [
						{
							taskName: 'EveningUse',
							humanActivationChance: 90,
							period: 1440,
							postponable: true,
							duration: 180,
							energyPerTick: 60.0,
							taskId: 1901,
						},
					],
				},
			],
		},
		{
			householdName: 'HH_20_Apartment',
			applianceConfigs: [
				{
					applianceName: 'Baseload_Devices',
					householdName: 'HH_20_Apartment',
					tasks: [
						{
							taskName: 'AlwaysOn',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 1440,
							energyPerTick: 150.0,
							taskId: 2000,
						},
					],
				},
				{
					applianceName: 'Lights',
					householdName: 'HH_20_Apartment',
					tasks: [
						{
							taskName: 'Evening',
							humanActivationChance: 100,
							period: 1440,
							postponable: false,
							duration: 300,
							energyPerTick: 10.0,
							taskId: 2001,
						},
					],
				},
			],
		},
	],
};

export { exampleConfig };
