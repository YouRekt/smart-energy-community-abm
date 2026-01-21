import {
	FieldDescription,
	FieldGroup,
	FieldLegend,
	FieldSet,
} from '@/components/ui/field';
import { withConfigForm } from '@/routes/config/-components/config-form/form-context';
import { defaultValues } from '@/routes/config/-components/config-form/schema';

const BatterySection = withConfigForm({
	defaultValues: defaultValues,
	render: ({ form }) => {
		return (
			<FieldSet>
				<FieldLegend>Battery Storage</FieldLegend>
				<FieldDescription>
					Configure the community battery parameters.
				</FieldDescription>
				<FieldGroup>
					<form.AppField
						name='batteryConfig.capacity'
						children={(field) => <field.CapacityInput />}
					/>
					<form.AppField
						name='batteryConfig.startingCharge'
						children={(field) => <field.StartingChargeInput />}
					/>
					<form.AppField
						name='batteryConfig.isPercentage'
						children={(field) => <field.IsPercentageInput />}
					/>
				</FieldGroup>
			</FieldSet>
		);
	},
});

export { BatterySection };
