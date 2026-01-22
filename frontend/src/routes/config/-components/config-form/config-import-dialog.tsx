import { Button } from '@/components/ui/button';
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
	DialogTrigger,
} from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';
import { formSchema } from '@/routes/config/-components/config-form/schema';
import { FileUp, Upload } from 'lucide-react';
import { useState } from 'react';
import { toast } from 'sonner';
import type { z } from 'zod';

interface ConfigImportDialogProps {
	onImport: (config: z.infer<typeof formSchema>) => void;
}

export function ConfigImportDialog({ onImport }: ConfigImportDialogProps) {
	const [isOpen, setIsOpen] = useState(false);
	const [jsonContent, setJsonContent] = useState('');
	const [error, setError] = useState<string | null>(null);

	const handleImport = () => {
		try {
			const parsed = JSON.parse(jsonContent);
			console.log(parsed);
			const result = formSchema.safeParse(parsed);
			console.log(result);

			if (!result.success) {
				setError('Invalid configuration: ' + result.error.message);
				return;
			}

			onImport(result.data);
			setIsOpen(false);
			setJsonContent('');
			setError(null);
			toast.success('Configuration imported successfully');
		} catch {
			setError('Invalid JSON format');
		}
	};

	const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
		const file = e.target.files?.[0];
		if (!file) return;

		const reader = new FileReader();
		reader.onload = (e) => {
			const content = e.target?.result as string;
			setJsonContent(content);
		};
		reader.readAsText(file);
	};

	return (
		<Dialog open={isOpen} onOpenChange={setIsOpen}>
			<DialogTrigger asChild>
				<Button variant='secondary' type='button'>
					<Upload className='mr-2 h-4 w-4' />
					Import JSON
				</Button>
			</DialogTrigger>
			<DialogContent className='sm:max-w-[425px]'>
				<DialogHeader>
					<DialogTitle>Import Configuration</DialogTitle>
					<DialogDescription>
						Paste your configuration JSON below or upload a file.
					</DialogDescription>
				</DialogHeader>
				<div className='grid w-full max-w-sm items-center gap-1.5'>
					<div className='flex items-center gap-2'>
						<Button
							variant='outline'
							className='w-full'
							onClick={() =>
								document.getElementById('file-upload')?.click()
							}>
							<FileUp className='mr-2 h-4 w-4' />
							Upload File
						</Button>
						<input
							id='file-upload'
							type='file'
							accept='.json'
							className='hidden'
							onChange={handleFileUpload}
						/>
					</div>
				</div>
				<div className='grid gap-4 py-4 max-h-[60vh] overflow-y-auto px-1 no-scrollbar'>
					<div className='relative'>
						<Textarea
							placeholder='Paste JSON here...'
							className='min-h-[200px] font-mono text-xs'
							value={jsonContent}
							onChange={(e) => {
								setJsonContent(e.target.value);
								setError(null);
							}}
						/>
					</div>
					{error && (
						<div className='text-sm text-destructive'>{error}</div>
					)}
				</div>
				<DialogFooter>
					<Button variant='outline' onClick={() => setIsOpen(false)}>
						Cancel
					</Button>
					<Button onClick={handleImport} disabled={!jsonContent}>
						Import
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
