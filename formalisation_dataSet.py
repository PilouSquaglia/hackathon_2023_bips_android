import pandas as pd

# Load the Excel file
file_path = 'data.xlsx'
xls = pd.ExcelFile(file_path)

sheet_names = xls.sheet_names
print(sheet_names)


# Function to clean and structure data for each sheet (village)
def clean_and_structure_data(sheet_name):
    # Load data from the sheet
    df = pd.read_excel(xls, sheet_name=sheet_name)

    # Removing the first row which contains headers like GPS, remplissage, etc.
    df = df.drop(0)

    # Renaming the columns for clarity
    columns = ['Jour', 'Date']
    for i in range(1, int((df.shape[1] - 2) / 3) + 1):
        columns += [f'Poubelle_{i}_GPS', f'Poubelle_{i}_Remplissage', f'Poubelle_{i}_Coeff_Touriste']
    df.columns = columns

    # Filling the GPS coordinates down the column as they are constant for each poubelle
    for i in range(1, int((df.shape[1] - 2) / 3) + 1):
        df[f'Poubelle_{i}_GPS'] = df[f'Poubelle_{i}_GPS'].fillna(method='ffill')

    return df

# Clean and structure data for the first village "Olmeta di Tuda"
olmeta_di_tuda_data = clean_and_structure_data('olmeta di tuda')
print(olmeta_di_tuda_data.head())


# Consolidating data for all villages

# Initialize an empty DataFrame to store all data
all_villages_data = pd.DataFrame()

# Loop through each sheet (village) and clean, structure, then append the data
for sheet in sheet_names:
    village_data = clean_and_structure_data(sheet)
    # Adding a column to identify the village
    village_data['Village'] = sheet
    # Append to the global DataFrame
    all_villages_data = all_villages_data.append(village_data, ignore_index=True)

# Display the first few rows of the consolidated DataFrame
all_villages_data.head()

# Define the path for the new consolidated Excel file
consolidated_file_path = 'Consolidated_Village_Data.xlsx'


# Ensure the date format is correct
all_villages_data['Date'] = pd.to_datetime(all_villages_data['Date']).dt.strftime('%Y-%m-%d')

# Save the data again with the corrected date format
all_villages_data.to_excel(consolidated_file_path, index=False)

# Provide the path for the corrected file
consolidated_file_path




depot = 42.60080491507166, 9.322923935409024