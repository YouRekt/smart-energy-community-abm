import {
	FieldDescription,
	FieldGroup,
	FieldLegend,
	FieldSet,
} from '@/components/ui/field';
import { withConfigForm } from '@/routes/config/-components/config-form/form-context';
import { defaultValues } from '@/routes/config/-components/config-form/schema';

const StrategySection = withConfigForm({
	defaultValues: defaultValues,
	render: ({ form }) => {
		return (
			<FieldSet>
				<FieldLegend>Strategy</FieldLegend>
				<FieldDescription>
					Choose the strategy for the simulation.
				</FieldDescription>
				<FieldGroup>
					<form.AppField
						name='strategyName'
						children={(field) => {
							return <field.StrategySelect />;
						}}
					/>
				</FieldGroup>
			</FieldSet>
		);
	},
});

export { StrategySection };
