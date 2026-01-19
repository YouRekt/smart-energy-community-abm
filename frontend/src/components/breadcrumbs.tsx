import {
	Breadcrumb,
	BreadcrumbItem,
	BreadcrumbList,
	BreadcrumbPage,
	BreadcrumbSeparator,
} from '@/components/ui/breadcrumb';
import { useMatches } from '@tanstack/react-router';
import { Fragment } from 'react/jsx-runtime';

const Breadcrumbs = () => {
	const matches = useMatches();

	const breadcrumbs = matches.filter(
		(match) => match.loaderData?.title || match.staticData?.title,
	);

	return (
		<Breadcrumb>
			<BreadcrumbList>
				{breadcrumbs.map((match, index, array) => {
					const label =
						match.loaderData?.title || match.staticData?.title;

					return (
						<Fragment key={match.id}>
							<BreadcrumbItem>
								<BreadcrumbPage>{label}</BreadcrumbPage>
							</BreadcrumbItem>
							{index < array.length - 1 && (
								<BreadcrumbSeparator className='hidden md:block' />
							)}
						</Fragment>
					);
				})}
				<BreadcrumbItem className='hidden md:block'>
					<BreadcrumbPage></BreadcrumbPage>
				</BreadcrumbItem>
			</BreadcrumbList>
		</Breadcrumb>
	);
};
export default Breadcrumbs;
