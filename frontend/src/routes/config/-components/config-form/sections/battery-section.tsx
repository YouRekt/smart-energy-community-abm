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
				<FieldGroup className='@3xl/field-group:flex-row'>
					<FieldGroup className='flex-2 @xl/field-group:flex-row'>
						<form.AppField
							name='batteryConfig.capacity'
							children={(field) => <field.CapacityInput />}
						/>
						<form.AppField
							name='batteryConfig.startingCharge'
							children={(field) => <field.StartingChargeInput />}
						/>
					</FieldGroup>
					<FieldGroup className='flex-1'>
						<form.AppField
							name='batteryConfig.isPercentage'
							children={(field) => <field.IsPercentageInput />}
						/>
					</FieldGroup>
				</FieldGroup>
			</FieldSet>
		);
	},
});

export { BatterySection };
